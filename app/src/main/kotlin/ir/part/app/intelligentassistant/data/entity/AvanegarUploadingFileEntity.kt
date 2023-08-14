package ir.part.app.intelligentassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvanegarUploadingFileEntity(
    @PrimaryKey
    val id: String,
    val createdAt: Long,
    val filePath: String,
    val title: String,
)