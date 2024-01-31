package ai.ivira.app.features.home.ui.home.version.model

import ai.ivira.app.features.home.data.entity.ChangelogEntity

data class ChangelogView(
    val versionCode: Int,
    val versionName: String,
    val releaseNotesTitles: List<String>
)

fun ChangelogEntity.toChangelogView() = ChangelogView(
    versionCode = versionCode,
    versionName = versionName,
    releaseNotesTitles = releaseNotes.map { it.title }

)