package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ColorKeyword
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhKeywordEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedFileEntity
import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import ai.ivira.app.features.imazh.data.entity.NFSWResultNetwork
import ai.ivira.app.features.imazh.data.entity.PainterKeyword
import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.features.imazh.data.entity.ValidateRequestNetwork
import ai.ivira.app.features.imazh.data.entity.attitudeKeyword
import ai.ivira.app.features.imazh.data.entity.lightAngleKeyword
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppException
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.ui.attachListItemToString
import android.os.SystemClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class ImazhRepository @Inject constructor(
    private val localDataSource: ImazhLocalDataSource,
    private val remoteDataSource: ImazhRemoteDataSource,
    private val networkHandler: NetworkHandler,
    private val randomPromptGenerator: RandomPromptGenerator
) {
    // it should be sortedMapOf because we need items based on this order
    fun getKeywords(): Flow<Map<String, Set<ImazhKeywordEntity>>> = flow {
        emit(
            LinkedHashMap<String, Set<ImazhKeywordEntity>>().apply {
                put("حال و هوا", attitudeKeyword)
                put("زاویه و نور", lightAngleKeyword)
                put("رنگ", ColorKeyword)
                put("نقاش", PainterKeyword)
            }
        )
    }

    fun getTrackingFiles() = localDataSource.getTrackingFiles()

    fun getAllArchiveFiles() = localDataSource.getAllArchiveFiles()

    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>> = localDataSource.getRecentHistory()
    suspend fun validatePromptAndConvertToImage(
        prompt: String,
        negativePrompt: String,
        keywords: List<ImazhKeywordEntity>,
        style: ImazhImageStyle
    ): AppResult<Boolean> {
        return when (val validateResult = validateAndTranslatePrompt(
            prompt = prompt,
            negativePrompt = negativePrompt,
            style = retrieveStyleKey(style)
        )) {
            is AppResult.Error -> {
                AppResult.Error(validateResult.error)
            }
            is AppResult.Success -> {
                if (validateResult.data.message.sfw) {
                    when (val textToImageResult = with(validateResult.data.message) {
                        convertTextToImage(
                            prompt = prompt,
                            englishPrompt = englishPrompt,
                            negativePrompt = negativePrompt,
                            englishNegativePrompt = englishNegativePrompt,
                            englishKeywords = keywords.map { it.englishKeyword },
                            keywordsNames = keywords.map { it.keywordName },
                            style = englishStyle
                        )
                    }) {
                        is AppResult.Success -> AppResult.Success(true)
                        is AppResult.Error -> AppResult.Error(textToImageResult.error)
                    }
                } else {
                    AppResult.Success(false)
                }
            }
        }
    }

    suspend fun regeneratePromptImage(processedItemId: Int): AppResult<Boolean> {
        val item = localDataSource.getProcessedFileEntity(processedItemId)
            ?: return AppResult.Error(
                AppException.IOException(
                    IllegalArgumentException("id($processedItemId) not found!")
                )
            )

        return when (
            val result = convertTextToImage(
                prompt = item.prompt,
                englishPrompt = item.englishPrompt,
                negativePrompt = item.negativePrompt,
                englishNegativePrompt = item.englishNegativePrompt,
                keywordsNames = item.keywords,
                englishKeywords = item.englishKeywords,
                style = item.style
            )
        ) {
            is AppResult.Success -> AppResult.Success(true)
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    private suspend fun convertTextToImage(
        prompt: String,
        englishPrompt: String,
        negativePrompt: String,
        englishNegativePrompt: String,
        englishKeywords: List<String>,
        keywordsNames: List<String>,
        style: String
    ): AppResult<TextToImageResult> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }
        val result = remoteDataSource.sendTextToImage(
            TextToImageRequestNetwork(
                prompt = englishPrompt.attachListItemToString(englishKeywords),
                negativePrompt = englishNegativePrompt,
                style = style
            )
        ).toAppResult()

        return when (result) {
            is AppResult.Success -> {
                localDataSource.insertTrackingFile(
                    ImazhTrackingFileEntity(
                        token = result.data.token,
                        processEstimation = result.data.estimationTime,
                        keywords = keywordsNames,
                        englishKeywords = englishKeywords,
                        prompt = prompt,
                        englishPrompt = englishPrompt,
                        negativePrompt = negativePrompt,
                        englishNegativePrompt = englishNegativePrompt,
                        style = style,
                        insertAt = TrackTime(
                            systemTime = PersianDate().time,
                            bootTime = SystemClock.elapsedRealtime()
                        ),
                        lastFailure = null
                    )
                )

                localDataSource.addPromptToHistory(
                    ImazhHistoryEntity(
                        prompt,
                        PersianDate().time
                    )
                )
                AppResult.Success(result.data)
            }
            is AppResult.Error -> {
                AppResult.Error(result.error)
            }
        }
    }

    suspend fun trackImageResult(fileToken: String): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = remoteDataSource.trackImageResult(fileToken)) {
            is ApiResult.Success -> {
                localDataSource.insertProcessedFromTracking(fileToken, result.data)
                AppResult.Success(Unit)
            }
            is ApiResult.Error -> {
                // TODO implement it
                AppResult.Error(result.error.toAppException())
            }
        }
    }

    private suspend fun validateAndTranslatePrompt(
        prompt: String,
        negativePrompt: String,
        style: String
    ): AppResult<NFSWResultNetwork> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val validateResult = remoteDataSource.validateAndTranslatePrompt(
            promptData = ValidateRequestNetwork(
                data = prompt,
                negativePrompt = negativePrompt,
                style = style,
                onlineTranslation = true
            )
        )) {
            is ApiResult.Error -> AppResult.Error(validateResult.error.toAppException())
            is ApiResult.Success -> AppResult.Success(validateResult.data)
        }
    }

    fun generateRandomPrompt(): String = randomPromptGenerator.generateRandomPrompt()

    fun getPhotoInfo(id: Int): Flow<ImazhProcessedFileEntity?> = localDataSource.getPhotoInfo(id)

    suspend fun getProcessedFileEntity(id: Int): ImazhProcessedFileEntity? =
        localDataSource.getProcessedFileEntity(id)

    fun getImageStyles(): Flow<List<ImazhImageStyle>> = flowOf(
        ImazhImageStyle.values().toList()
    )

    suspend fun deleteProcessedFile(id: Int) = localDataSource.deleteProcessedFile(id)

    suspend fun deleteTrackingFile(token: String) = localDataSource.deleteTrackingFile(token)

    private fun retrieveStyleKey(selectedStyle: ImazhImageStyle): String {
        return if (selectedStyle == ImazhImageStyle.None) {
            getRandomStyleFrom(
                listOf(
                    ImazhImageStyle.Cinematic,
                    ImazhImageStyle.Comic,
                    ImazhImageStyle.DigitalArt,
                    ImazhImageStyle.Anime
                )
            ).key
        } else {
            selectedStyle.key
        }
    }

    private fun getRandomStyleFrom(styles: List<ImazhImageStyle>): ImazhImageStyle {
        if (styles.isEmpty()) return ImazhImageStyle.None

        val random = Random(seed = System.currentTimeMillis())
            .nextInt(0, styles.size * 100)
            .mod(styles.size)
        return styles[random]
    }

    suspend fun downloadFile(
        id: Int,
        url: String,
        fileName: String,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): AppResult<Unit> {
        return if (networkHandler.hasNetworkConnection()) {
            val file = File(fileName)
            val result = remoteDataSource.downloadFile(url, file, progress).toAppResult()

            if (file.exists()) {
                localDataSource.updateFilePath(id, file.absolutePath)
            }

            when (result) {
                is AppResult.Success -> AppResult.Success(Unit)
                is AppResult.Error -> AppResult.Error(result.error)
            }
        } else {
            AppResult.Error(AppException.NetworkConnectionException())
        }
    }
}