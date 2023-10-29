package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechResponseNetwork(
    val data: TextToSpeechItemResponseNetwork
)

@JsonClass(generateAdapter = true)
data class TextToSpeechItemResponseNetwork(
    val checksum: String,
    val filePath: String
)