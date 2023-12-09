package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemLongNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechLongRequestNetwork
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
    init {
        System.loadLibrary("vira")
    }

    suspend fun getSpeechFile(
        text: String,
        speakerType: String
    ): ApiResult<TextToSpeechItemNetwork> {
        val result = service.getTextToSpeech(
            token = sak(),
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

    suspend fun getSpeechFileLong(
        text: String,
        speakerType: String
    ): ApiResult<TextToSpeechItemLongNetwork> {
        val result = service.getTextToSpeechLong(
            token = sak(),
            getSpeechBody = TextToSpeechLongRequestNetwork(
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
    ): ApiResult<Unit> {
        return when (
            val result = downloadFileRequest.downloadFile(
                url = url,
                file = file,
                token = sak(),
                progress = progress
            )
        ) {
            is Success -> Success(Unit)
            is Error -> Error(result.error)
        }
    }

    private external fun sak(): String
}