package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.CheckSpeechNetwork
import ai.ivira.app.features.avasho.data.entity.CheckSpeechRequestNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemLongNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechLongRequestNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechRequestNetwork
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AvashoService {
    @POST("sahab/gateway/service/speech-synthesys@3/speech-synthesys")
    suspend fun getTextToSpeech(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("ApiKey") token: String,
        @Body getSpeechBody: TextToSpeechRequestNetwork
    ): ApiResult<ViraNetwork<TextToSpeechItemNetwork>>

    @POST("sahab/gateway/service/speech-synthesys@3/longText")
    suspend fun getTextToSpeechLong(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("ApiKey") token: String,
        @Body getSpeechBody: TextToSpeechLongRequestNetwork
    ): ApiResult<ViraNetwork<TextToSpeechItemLongNetwork>>

    @GET("sahab/gateway/service/speech-synthesys@3/trackingFile/{token}")
    suspend fun trackLargeTextResult(
        @Header("ApiKey") token: String,
        @Path("token") fileToken: String
    ): ApiResult<ViraNetwork<TextToSpeechItemNetwork>>

    @POST("sahab/gateway/service/loghman/loghmans")
    suspend fun checkSpeech(
        @Header("ApiKey") token: String,
        @Body checkSpeechBody: CheckSpeechRequestNetwork
    ): ApiResult<ViraNetwork<CheckSpeechNetwork>>
}