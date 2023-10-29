package ai.ivira.app.features.avasho.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvashoProcessedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val checksum: String,
    val filePath: String,
    val fileName: String,
    val text: String,
    val createdAt: Long
)