package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechRequestNetwork
import ai.ivira.app.utils.common.file.DownloadFileRequest
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import java.io.File
import javax.inject.Inject

class AvashoRemoteDataSource @Inject constructor(
    private val service: AvashoService,
    private val downloadFileRequest: DownloadFileRequest
) {
    suspend fun getSpeechFile(
        text: String,
        speakerType: String
    ): ApiResult<TextToSpeechNetwork<TextToSpeechItemNetwork>> {
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

    suspend fun downloadFile(
        url: String,
        file: File,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ) {
        downloadFileRequest.downloadFile(url, file, progress)
    }
}