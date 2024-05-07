package ai.ivira.app.features.login.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyOtpNetworkRequest(
    val mobile: String,
    val otp: String
)