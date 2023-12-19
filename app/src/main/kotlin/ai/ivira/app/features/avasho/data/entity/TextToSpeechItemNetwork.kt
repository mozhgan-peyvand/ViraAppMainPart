package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechItemNetwork(val filePath: String)