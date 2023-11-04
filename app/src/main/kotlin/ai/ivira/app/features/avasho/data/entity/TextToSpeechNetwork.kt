package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechNetwork<out T>(
    val data: T
)

@JsonClass(generateAdapter = true)
data class TextToSpeechItemNetwork(
    val checksum: String,
    val filePath: String
)