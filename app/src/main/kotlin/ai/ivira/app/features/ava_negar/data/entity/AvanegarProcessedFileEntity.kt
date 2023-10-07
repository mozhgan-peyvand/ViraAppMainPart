package ai.ivira.app.features.ava_negar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvanegarProcessedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val text: String,
    val createdAt: Long,
    val filePath: String,
    val isSeen: Boolean
)