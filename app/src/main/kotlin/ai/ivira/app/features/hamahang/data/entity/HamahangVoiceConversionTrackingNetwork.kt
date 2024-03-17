package ai.ivira.app.features.hamahang.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HamahangVoiceConversionTrackingNetwork(
    val filePath: String
)