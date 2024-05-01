package ai.ivira.app.features.config.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfigTileNetwork(
    val message: String,
    val status: Boolean
)