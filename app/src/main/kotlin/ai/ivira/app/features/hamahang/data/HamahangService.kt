package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangVoiceConversionNetwork
import ai.ivira.app.features.hamahang.data.entity.HamahangVoiceConversionTrackingNetwork
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

interface HamahangService {
    @Multipart
    @POST("sahab/gateway/service/voiceConversion/file")
    suspend fun voiceConversion(
        @Header("ApiKey") token: String,
        @Part file: MultipartBody.Part,
        @Part("speaker") speaker: RequestBody
    ): ApiResult<ViraNetwork<HamahangVoiceConversionNetwork>>

    @GET("sahab/gateway/service/voiceConversion/trackingFile/{token}")
    suspend fun trackVoiceConversion(
        @Header("ApiKey") token: String,
        @Path("token") fileToken: String
    ): ApiResult<ViraNetwork<HamahangVoiceConversionTrackingNetwork>>
}