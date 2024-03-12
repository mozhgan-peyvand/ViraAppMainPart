package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToImageItemNetwork(
    val filePath: String,
    val nsfw: Boolean?
)