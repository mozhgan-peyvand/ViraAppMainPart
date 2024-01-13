package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToImageRequestNetwork(
    val prompt: String,
    @Json(name = "negative_prompt")
    val negativePrompt: String,
    val style: String
)