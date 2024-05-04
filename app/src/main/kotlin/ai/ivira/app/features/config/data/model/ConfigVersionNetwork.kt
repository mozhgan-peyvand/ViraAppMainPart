package ai.ivira.app.features.config.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfigVersionNetwork(
    val isForce: Int,
    val osType: String,
    val releaseNote: List<ConfigVersionReleaseNoteNetwork>,
    val versionName: String,
    val versionNumber: Int
)

@JsonClass(generateAdapter = true)
data class ConfigVersionReleaseNoteNetwork(
    val type: Int,
    val title: String
)