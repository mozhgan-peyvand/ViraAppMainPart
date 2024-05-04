package ai.ivira.app.features.config.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfigObjectNetwork<T>(
    val name: String,
    val value: T
)