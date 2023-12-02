package ai.ivira.app.features.home.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class VersionDto(

    @Embedded
    val versionEntity: VersionEntity,

    @Relation(parentColumn = "versionNumber", entityColumn = "versionNumber")
    val releaseNotes: List<ReleaseNoteEntity>
)