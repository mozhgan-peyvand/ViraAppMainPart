package ai.ivira.app.features.ava_negar.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvanegarResponseNetwork(
    val data: AvanegarProcessedTextNetwork,
    val requestId: String,
    val status: String
)