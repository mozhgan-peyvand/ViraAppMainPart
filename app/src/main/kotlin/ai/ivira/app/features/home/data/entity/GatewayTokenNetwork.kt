package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GatewayTokenNetwork(
    val token: String
)