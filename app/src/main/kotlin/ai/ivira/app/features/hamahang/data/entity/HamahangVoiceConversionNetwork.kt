package ai.ivira.app.features.hamahang.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HamahangVoiceConversionNetwork(
    val estimationTime: Int,
    val token: String
)