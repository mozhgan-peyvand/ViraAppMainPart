package ir.part.app.intelligentassistant.features.ava_negar.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LargeFileDataNetwork(
    val token: String
)