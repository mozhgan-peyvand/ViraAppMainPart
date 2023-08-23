package ir.part.app.intelligentassistant.features.ava_negar.data

import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarResponseNetwork
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.LargeFileResponseNetwork
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.Resource
import ir.part.app.intelligentassistant.utils.data.api_result.ApiResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AvanegarService {

    @Multipart
    @POST("speechRecognition/v1/file")
    suspend fun audioToTextBelowSixtySecond(
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): ApiResult<Resource<AvanegarResponseNetwork>>

    @Multipart
    @POST("speechRecognition/v1/largeFile")
    suspend fun audioToTextAboveSixtySecond(
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): ApiResult<Resource<LargeFileResponseNetwork>>

    @GET("speechRecognition/v1/trackingText/{token}")
    suspend fun trackLargeFileResult(
        @Path("token") fileToken: String,
    ): ApiResult<AvanegarResponseNetwork>
}