package ai.ivira.app.features.home.ui.home.version.model

import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity

data class ReleaseNoteView(
    val type: Int,
    val title: String
)

fun ConfigVersionReleaseNoteEntity.toReleaseNoteView() = ReleaseNoteView(
    type = type,
    title = title
)