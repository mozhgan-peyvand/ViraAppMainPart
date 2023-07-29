package ir.part.app.intelligentassistant.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvanegarProcessedTextNetwork(
    val result: String
)