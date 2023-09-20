package ir.part.app.intelligentassistant.features.ava_negar.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvanegarUploadingFileEntity(
    @PrimaryKey
    val id: String,
    val createdAt: Long,
    val filePath: String,
    val title: String,
    val fileDuration: Long
)