package ai.ivira.app.features.ava_negar.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvanegarProcessedTextNetwork(
    val result: String
)