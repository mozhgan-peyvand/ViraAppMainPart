package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.NFSWResultNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageRequestNetwork
import ai.ivira.app.features.imazh.data.entity.TextToImageResult
import ai.ivira.app.features.imazh.data.entity.ValidateRequestNetwork
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ImazhService {
    @POST
    suspend fun sendTextToImage(
        @Url url: String,
        @Header("ApiKey") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body photoDescribe: TextToImageRequestNetwork
    ): ApiResult<ViraNetwork<TextToImageResult>>

    @POST
    suspend fun validateAndTranslatePrompt(
        @Url url: String,
        @Header("ApiKey") apiKey: String,
        @Body data: ValidateRequestNetwork
    ): ApiResult<ViraNetwork<NFSWResultNetwork>>
}