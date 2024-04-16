package ai.ivira.app.features.login.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SendOtpRequestNetwork(
    val mobile: String
)