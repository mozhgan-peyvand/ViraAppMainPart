package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedTextNetwork
import ai.ivira.app.features.ava_negar.data.entity.LargeFileDataNetwork
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AvanegarService {
    @Multipart
    @POST("sahab/gateway/service/pr-speech-asr-sahab/file")
    suspend fun audioToTextBelowSixtySecond(
        @Header("ApiKey") token: String,
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): ApiResult<ViraNetwork<AvanegarProcessedTextNetwork>>

    @Multipart
    @POST("sahab/gateway/service/pr-speech-asr-sahab-largefile/largeFile")
    suspend fun audioToTextAboveSixtySecond(
        @Header("ApiKey") token: String,
        @Part file: MultipartBody.Part,
        @Part("estimation") estimation: Boolean,
        @Part("language") language: RequestBody
    ): ApiResult<ViraNetwork<LargeFileDataNetwork>>

    @GET("sahab/gateway/service/pr-speech-asr-sahab-largefile/largeFile/{token}")
    suspend fun trackLargeFileResult(
        @Header("ApiKey") token: String,
        @Path("token") fileToken: String
    ): ApiResult<ViraNetwork<AvanegarProcessedTextNetwork>>
}