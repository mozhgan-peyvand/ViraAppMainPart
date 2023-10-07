package ai.ivira.app.features.ava_negar.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LargeFileResponseNetwork(
    val data: LargeFileDataNetwork
)