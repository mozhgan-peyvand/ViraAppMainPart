package ai.ivira.app.features.config.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TileConfigItemNetwork(
    val name: String,
    val value: TileConfigStatusNetwork
) {
    fun toTileConfigEntity() = TileConfigEntity(
        name = name,
        message = value.message,
        status = value.status
    )
}

@JsonClass(generateAdapter = true)
data class TileConfigStatusNetwork(
    val message: String,
    val status: Boolean
)