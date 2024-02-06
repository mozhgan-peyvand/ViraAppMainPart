package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NFSWResultNetwork(
    val message: NFSWMessageNetwork,
    val status: String
)

@JsonClass(generateAdapter = true)
data class NFSWMessageNetwork(
    @Json(name = "SFW")
    val sfw: Boolean,
    @Json(name = "english_negative_prompt")
    val englishNegativePrompt: String,
    @Json(name = "english_prompt")
    val englishPrompt: String,
    @Json(name = "english_style")
    val englishStyle: String,
    @Json(name = "negative_prompt")
    val negativePrompt: String,
    val prompt: String,
    val style: String
)