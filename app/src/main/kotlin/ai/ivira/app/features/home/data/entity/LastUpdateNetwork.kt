package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastUpdateNetwork(
    val name: String,
    val value: Long
)