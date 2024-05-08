package ai.ivira.app.features.hamahang.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HamahangProcessedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val fileUrl: String,
    val filePath: String,
    val inputFilePath: String,
    val speaker: String,
    val createdAt: Long,
    val isSeen: Boolean
)