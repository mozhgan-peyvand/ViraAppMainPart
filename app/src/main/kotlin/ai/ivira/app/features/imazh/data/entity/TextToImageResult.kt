package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToImageResult(
    val token: String,
    val estimationTime: Int?
)