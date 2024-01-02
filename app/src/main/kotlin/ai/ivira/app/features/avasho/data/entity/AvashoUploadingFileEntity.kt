package ai.ivira.app.features.avasho.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvashoUploadingFileEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val text: String,
    val speaker: String,
    val createdAt: Long
)