package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemLongNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechItemNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechLongRequestNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechRequestNetwork
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
}