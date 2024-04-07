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
    // FIXME: must be replaced with logged in token! (after gateway request come in)
    private val token = "86c854f5c0ea118c0b85ba9db3c756396425e9fb23cc31e1000fcfba30616dec1f684e9f2828a57856ab71c20021041acf13012880326e24296e1662b32eb775"

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
                url = "${bu()}sahab/gateway$url",
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

    private external fun bu(): String
}