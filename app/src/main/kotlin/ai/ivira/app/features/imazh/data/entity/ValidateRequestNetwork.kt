package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ValidateRequestNetwork(
    val data: String,
    val style: String,
    @Json(name = "negative_prompt")
    val negativePrompt: String = "",
    @Json(name = "online_translation")
    val onlineTranslation: Boolean
)