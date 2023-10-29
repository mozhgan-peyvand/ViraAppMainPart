package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.TextToSpeechRequestNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechResponseNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import javax.inject.Inject

class AvashoRemoteDataSource @Inject constructor(
    private val service: AvashoService
) {
    suspend fun getSpeechFile(
        text: String,
        speakerType: String
    ): ApiResult<TextToSpeechResponseNetwork> {
        val result = service.getTextToSpeech(
            getSpeechBody = TextToSpeechRequestNetwork(
                data = text,
                speaker = speakerType
            )
        )
        return when (result) {
            is Success -> Success(result.data.data)
            is Error -> Error(result.error)
        }
    }
}