package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.ava_negar.data.AvanegarRemoteDataSource
import ai.ivira.app.features.avasho.data.AvashoRemoteDataSource
import ai.ivira.app.features.hamahang.data.entity.HamahangVoiceConversionNetwork
import ai.ivira.app.utils.common.file.DownloadFileRequest
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.makeRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class HamahangRemoteDataSource @Inject constructor(
    private val service: HamahangService,
    private val downloadFileRequest: DownloadFileRequest,
    private val avanegarRemoteDataSource: AvanegarRemoteDataSource,
    private val avashoRemoteDataSource: AvashoRemoteDataSource
) {
    private val token = "2f632ed518a52e2b972a4a81f01f6f58fcd38f9ae324769ec2a5f1e9642ac7accac10f894eb2b7fd807a86934b8b4443f42c30f5decf54745d3d4e0716dacc5a"

    suspend fun voiceConversion(
        multiPartFile: MultipartBody.Part,
        speaker: RequestBody
    ): ApiResult<HamahangVoiceConversionNetwork> {
        val result = service.voiceConversion(
            token = token,
            file = multiPartFile,
            speaker = speaker
        )

        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun trackVoiceConversion(
        fileToken: String
    ): ApiResult<String> {
        val result = service.trackVoiceConversion(
            token = token,
            fileToken = fileToken
        )

        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data.filePath)
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
                url = "http://192.168.33.21:3002$url",
                file = file,
                token = token,
                progress = progress
            )
        ) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun checkAudioValidity(
        multiPartFile: MultipartBody.Part,
        language: RequestBody
    ) = makeRequest(
        action = {
            avanegarRemoteDataSource.audioToTextBelowSixtySecond(
                multiPartFile = multiPartFile,
                language = language
            )
        },
        onSuccess = {
            makeRequest(
                action = { avashoRemoteDataSource.checkSpeech(data) },
                onSuccess = { ApiResult.Success(data) }
            )
        }
    )
}