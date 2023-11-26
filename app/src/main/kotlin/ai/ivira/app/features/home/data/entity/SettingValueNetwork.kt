package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SettingValueNetwork(
    val isForce: Int,
    val osType: String,
    val releaseNote: List<ReleaseNoteNetwork>,
    val versionName: String,
    val versionNumber: Int
)