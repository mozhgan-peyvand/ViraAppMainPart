package ai.ivira.app.features.login.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val meta: Meta
)

@JsonClass(generateAdapter = true)
data class Meta(
    val code: String
)