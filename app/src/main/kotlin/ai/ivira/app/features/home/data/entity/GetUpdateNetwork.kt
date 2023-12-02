package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUpdateNetwork(
    @Json(name = "setting")
    val versions: List<SettingNetwork>
)