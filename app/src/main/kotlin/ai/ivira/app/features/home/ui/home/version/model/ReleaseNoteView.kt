package ai.ivira.app.features.home.ui.home.version.model

import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity

data class ReleaseNoteView(
    val type: Int,
    val title: String
)

fun ReleaseNoteEntity.toReleaseNoteView() = ReleaseNoteView(
    type = type,
    title = title
)