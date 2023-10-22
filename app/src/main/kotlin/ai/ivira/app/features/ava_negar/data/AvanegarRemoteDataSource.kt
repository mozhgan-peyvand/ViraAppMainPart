package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.LargeFileDataNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AvanegarRemoteDataSource @Inject constructor(
    private val avanegarService: AvanegarService
) {
    suspend fun audioToTextBelowSixtySecond(
        multiPartFile: MultipartBody.Part,
        language: RequestBody
    ): ApiResult<String> {
        val result = avanegarService.audioToTextBelowSixtySecond(
            file = multiPartFile,
            language = language
        )

        return when (result) {
            is Success -> Success(result.data.data.result)
            is Error -> Error(result.error)
        }
    }

    suspend fun audioToTextAboveSixtySecond(
        multiPartFile: MultipartBody.Part,
        language: RequestBody
    ): ApiResult<LargeFileDataNetwork> {
        val result = avanegarService.audioToTextAboveSixtySecond(
            file = multiPartFile,
            estimation = true,
            language = language
        )

        return when (result) {
            is Success -> Success(result.data.data)
            is Error -> Error(result.error)
        }
    }

    suspend fun trackLargeFileResult(
        fileToken: String
    ): ApiResult<String> {
        val result = avanegarService.trackLargeFileResult(
            fileToken = fileToken
        )

        return when (result) {
            is Success -> Success(result.data.data.result)
            is Error -> Error(result.error)
        }
    }
}