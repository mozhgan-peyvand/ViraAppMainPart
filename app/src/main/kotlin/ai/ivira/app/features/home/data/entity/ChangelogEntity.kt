package ai.ivira.app.features.home.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangelogEntity(
    val versionCode: Int,
    val versionName: String,
    val releaseNotes: List<ReleaseNoteChangeLogEntity>
)

@JsonClass(generateAdapter = true)
data class ReleaseNoteChangeLogEntity(
    val type: Int,
    val title: String
)