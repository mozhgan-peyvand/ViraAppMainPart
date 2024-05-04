package ai.ivira.app.features.home.data.entity

import ai.ivira.app.features.config.data.model.ConfigVersionEntity
import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity
import androidx.room.Embedded
import androidx.room.Relation

data class VersionDto(

    @Embedded
    val versionEntity: ConfigVersionEntity,

    @Relation(parentColumn = "versionNumber", entityColumn = "versionNumber")
    val releaseNotes: List<ConfigVersionReleaseNoteEntity>
)