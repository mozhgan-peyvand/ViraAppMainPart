package ai.ivira.app.features.avasho.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckSpeechRequestNetwork(
    val data: String,
    @Json(name = "lemma_flag")
    val lemmaFlag: Boolean = false
)