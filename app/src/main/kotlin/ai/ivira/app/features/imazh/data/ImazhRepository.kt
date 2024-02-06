package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ColorKeyword
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhKeywordEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity
import ai.ivira.app.features.imazh.data.entity.PainterKeyword
import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.features.imazh.data.entity.attitudeKeyword
import ai.ivira.app.features.imazh.data.entity.lightAngleKeyword
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.IMAZH_FOLDER_PATH
import ai.ivira.app.utils.common.file.PNG_EXTENSION
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.ui.attachListItemToString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class ImazhRepository @Inject constructor(
    private val localDataSource: ImazhLocalDataSource,
    private val remoteDataSource: ImazhRemoteDataSource,
    private val networkHandler: NetworkHandler,
    private val randomPromptGenerator: RandomPromptGenerator,
    private val fileOperationHelper: FileOperationHelper
) {
    // it should be sortedMapOf because we need items based on this order
    fun getKeywords(): Flow<Map<String, Set<ImazhKeywordEntity>>> = flow {
        emit(
            LinkedHashMap<String, Set<ImazhKeywordEntity>>().apply {
                put("حال و هوا", attitudeKeyword)
                put("زاویه و نور", lightAngleKeyword)
                put("نقاش", PainterKeyword)
                put("رنگ", ColorKeyword)
            }
        )
    }

    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>> = localDataSource.getRecentHistory()
    suspend fun convertTextToImage(
        prompt: String,
        negativePrompt: String,
        keywords: List<ImazhKeywordEntity>,
        style: ImazhImageStyle
    ): AppResult<TextToImageResult> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }
        val result = remoteDataSource.sendTextToImage(
            TextToImageRequestNetwork(
                prompt.attachListItemToString(keywords.map { it.englishKeyword }),
                negativePrompt = negativePrompt,
                style = retrieveStyleKey(style)
            )
        ).toAppResult()

        return when (result) {
            is AppResult.Success -> {
                val file = fileOperationHelper.getFile(
                    fileName = "${System.currentTimeMillis()}_${UUID.randomUUID().mostSignificantBits}",
                    path = IMAZH_FOLDER_PATH,
                    extension = PNG_EXTENSION
                )

                localDataSource.addImageToDataBase(
                    imagePath = result.data.message.imagePath.removePrefix("/"),
                    keywords = keywords.map { it.keywordName },
                    prompt = prompt,
                    negativePrompt = negativePrompt,
                    style = style.key,
                    createdAt = PersianDate().time,
                    filePath = file.absolutePath
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

    fun generateRandomPrompt(): String = randomPromptGenerator.generateRandomPrompt()

    fun getPhotoInfo(id: Int): Flow<ImazhProcessedEntity?> = localDataSource.getPhotoInfo(id)

    fun getImageStyles(): Flow<List<ImazhImageStyle>> = flowOf(
        ImazhImageStyle.values().toList()
    )

    suspend fun deletePhotoInfo(id: Int) = localDataSource.deletePhotoInfo(id)

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

    fun getAllProcessedFiles() = localDataSource.getAllProcessedFiles()

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

    fun sai() = remoteDataSource.sai()

    fun bi() = remoteDataSource.bi()
}