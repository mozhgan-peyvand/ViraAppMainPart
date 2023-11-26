package ai.ivira.app.features.home.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReleaseNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val versionNumber: Int,
    val type: Int,
    val title: String
)