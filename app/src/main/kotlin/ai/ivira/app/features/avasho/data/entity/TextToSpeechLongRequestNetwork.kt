package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechLongRequestNetwork(
    val data: String,
    val speaker: String
)