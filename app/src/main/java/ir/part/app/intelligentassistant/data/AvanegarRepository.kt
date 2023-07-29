package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.utils.data.api_result.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class AvanegarRepository @Inject constructor(
    private val avanegarRemoteDataSource: AvanegarRemoteDataSource
) {

    suspend fun audioToTextBelowSixtySecond(
        language: String,
        multipartBodyFile: MultipartBody.Part
    ): ApiResult<String> {

        return avanegarRemoteDataSource.audioToTextBelowSixtySecond(
            multiPartFile = multipartBodyFile,
            language = makeRequestBody(language)
        )
    }

    suspend fun audioToTextAboveSixtySecond(
        language: String,
        multipartBodyFile: MultipartBody.Part
    ): ApiResult<String> {
        return avanegarRemoteDataSource.audioToTextAboveSixtySecond(
            multiPartFile = multipartBodyFile,
            language = makeRequestBody(language)
        )
    }

    suspend fun trackLargeFileResult(fileToken: String): ApiResult<String> {
        return avanegarRemoteDataSource.trackLargeFileResult(
            fileToken = fileToken
        )
    }

    private fun makeRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}