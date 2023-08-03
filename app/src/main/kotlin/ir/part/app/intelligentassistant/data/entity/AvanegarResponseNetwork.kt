package ir.part.app.intelligentassistant.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvanegarResponseNetwork(
    val data: AvanegarProcessedTextNetwork,
    val requestId: String,
    val status: String
)