package ai.ivira.app.features.avasho.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvashoProcessedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fileUrl: String,
    val filePath: String,
    val fileName: String,
    val text: String,
    val createdAt: Long,
    val isDownloading: Boolean,
    val isSeen: Boolean
)