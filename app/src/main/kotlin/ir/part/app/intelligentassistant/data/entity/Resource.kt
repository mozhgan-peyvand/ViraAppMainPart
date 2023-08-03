package ir.part.app.intelligentassistant.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Resource<out T>(
    val data: T
)