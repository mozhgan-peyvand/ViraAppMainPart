package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToSpeechItemLongNetwork(
    val estimatedProcessTime: String,
    val token: String
)