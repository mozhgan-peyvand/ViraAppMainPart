package ai.ivira.app.utils.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ViraNetwork<out T>(
    val data: T
)