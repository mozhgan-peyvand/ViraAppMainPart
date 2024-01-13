package ai.ivira.app.features.imazh.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TextToImageResult(val message: Message)

@JsonClass(generateAdapter = true)
data class Message(
    val imagePath: String
)