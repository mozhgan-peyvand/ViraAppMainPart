package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.NFSWResultNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.features.imazh.data.entity.ValidateRequestNetwork
import ai.ivira.app.utils.common.file.DownloadFileRequest
import ai.ivira.app.utils.data.api_result.ApiResult
import java.io.File
import javax.inject.Inject

class ImazhRemoteDataSource @Inject constructor(
    private val imazhService: ImazhService,
    private val downloadFileRequest: DownloadFileRequest
) {
    init {
        System.loadLibrary("vira")
    }

    suspend fun sendTextToImage(photoDescribe: TextToImageRequestNetwork): ApiResult<TextToImageResult> {
        return when (val result = imazhService.sendTextToImage(
            photoDescribe = photoDescribe,
            apiKey = iak()
        )) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun trackImageResult(fileToken: String): ApiResult<String> {
        val result = imazhService.trackImageResult(
            apiKey = iak(),
            fileToken = fileToken
        )

        return when (result) {
            is ApiResult.Error -> ApiResult.Error(result.error)
            is ApiResult.Success -> ApiResult.Success(result.data.data.filePath)
        }
    }

    suspend fun validateAndTranslatePrompt(
        promptData: ValidateRequestNetwork
    ): ApiResult<NFSWResultNetwork> {
        return when (
            val result = imazhService.validateAndTranslatePrompt(
                apiKey = sai(),
                url = bi() + "/service/nsfwDetector/data",
                data = promptData
            )
        ) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun downloadFile(
        url: String,
        file: File,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): ApiResult<Unit> {
        return when (
            val result = downloadFileRequest.downloadFile(
                url = bu() + "/sahab/gateway" + url,
                file = file,
                token = iak(),
                progress = progress
            )
        ) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    external fun sai(): String
    external fun bi(): String
    private external fun iak(): String
    private external fun bu(): String
}