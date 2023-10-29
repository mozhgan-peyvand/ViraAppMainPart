package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.ava_negar.data.entity.Resource
import ai.ivira.app.features.avasho.data.entity.TextToSpeechRequestNetwork
import ai.ivira.app.features.avasho.data.entity.TextToSpeechResponseNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

const val Token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzeXN0ZW0iOiJzYWhhYiIsImNyZWF0ZVRpbWUiOiIxNDAyMDYwODEwMTM0Mjc0OSIsInVuaXF1ZUZpZWxkcyI6eyJ1c2VybmFtZSI6Ijc2YjY1ZDRmLTI5OWYtNGYyYy1hMzlmLTVjYTg3OGQyZmU1YSJ9LCJkYXRhIjp7InNlcnZpY2VJRCI6ImRmNTNhNzgwLTIxNTgtNDUyNC05MjQ3LWM2ZjBiYWQzZTc3MCIsInJhbmRvbVRleHQiOiJtcjJzdyJ9LCJncm91cE5hbWUiOiJmNzlmZWZmMjljNDA5NTliY2U4Njk2NmE0MDIyNjc2ZSJ9.Wwe4ohhl2MSpilUCOudNhNPDIsCViZt3-d-98JNF2XE"

interface AvashoService {
    @POST("TextToSpeech/v1/speech-synthesys")
    suspend fun getTextToSpeech(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("gateway-token") token: String = Token,
        @Body getSpeechBody: TextToSpeechRequestNetwork
    ): ApiResult<Resource<TextToSpeechResponseNetwork>>
}