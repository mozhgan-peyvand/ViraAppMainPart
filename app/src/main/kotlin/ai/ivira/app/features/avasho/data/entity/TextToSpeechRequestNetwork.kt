package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechRequestNetwork(
    val data: String,
    val speaker: String,
    val filePath: String = "true",
    val base64: String = "0",
    val checksum: String = "1"
)