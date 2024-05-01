package ai.ivira.app.features.config.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigVersionReleaseNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val versionNumber: Int,
    val type: Int,
    val title: String
)

fun ConfigVersionReleaseNoteNetwork.totoConfigVersionReleaseNoteEntity(
    versionNumber: Int
): ConfigVersionReleaseNoteEntity {
    return ConfigVersionReleaseNoteEntity(
        id = 0, // auto generated. must be 0
        versionNumber = versionNumber,
        type = type,
        title = title
    )
}