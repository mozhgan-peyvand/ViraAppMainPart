package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.ui.attachListItemToString
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImazhRepository @Inject constructor(
    private val localDataSource: ImazhLocalDataSource,
    private val remoteDataSource: ImazhRemoteDataSource,
    private val networkHandler: NetworkHandler,
    private val randomPromptGenerator: RandomPromptGenerator
) {
    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>> = localDataSource.getRecentHistory()
    suspend fun convertTextToImage(
        prompt: String,
        negativePrompt: String,
        keywords: List<String>,
        style: String
    ): AppResult<TextToImageResult> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }
        val result = remoteDataSource.sendTextToImage(
            TextToImageRequestNetwork(
                prompt.attachListItemToString(keywords),
                negativePrompt = negativePrompt,
                style = style
            )
        ).toAppResult()

        return when (result) {
            is AppResult.Success -> {
                localDataSource.addImageToDataBase(
                    imagePath = result.data.message.imagePath,
                    keywords = keywords,
                    prompt = prompt,
                    negativePrompt = negativePrompt,
                    style = style
                )
                AppResult.Success(result.data)
            }
            is AppResult.Error -> {
                AppResult.Error(result.error)
            }
        }
    }

    fun generateRandomPrompt(): String = randomPromptGenerator.generateRandomPrompt()
}