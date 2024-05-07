package ai.ivira.app.features.login.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpNetworkResponse(val token: String)