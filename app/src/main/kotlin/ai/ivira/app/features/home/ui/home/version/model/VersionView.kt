package ai.ivira.app.features.home.ui.home.version.model

import ai.ivira.app.features.home.data.entity.VersionDto

data class VersionView(
    val isForce: Boolean,
    val releaseNote: List<ReleaseNoteView>,
    val versionName: String,
    val versionNumber: Int
)

fun VersionDto.toVersionView() = VersionView(
    isForce = versionEntity.isForce,
    releaseNote = releaseNotes.map { releaseNoteView ->
        releaseNoteView.toReleaseNoteView()
    },
    versionName = versionEntity.versionName,
    versionNumber = versionEntity.versionNumber
)