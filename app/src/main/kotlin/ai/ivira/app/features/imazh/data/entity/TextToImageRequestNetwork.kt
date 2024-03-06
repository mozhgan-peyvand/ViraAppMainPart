package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToImageRequestNetwork(
    val prompt: String,
    val negativePrompt: String = "",
    val style: String
)