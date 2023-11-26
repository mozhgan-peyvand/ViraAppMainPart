package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostUpdateGatewayToken(
    val system: String,
    val authenticationPack: UpdateGatewayAuthenticationPack
)

@JsonClass(generateAdapter = true)
data class UpdateGatewayAuthenticationPack(
    val username: String,
    val password: String
)