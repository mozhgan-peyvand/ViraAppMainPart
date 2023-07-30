package ir.part.app.intelligentassistant.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AvanegarTrackingFileEntity(
    @PrimaryKey
    val token: String,
    val filePath: String,
    val title: String,
    val createdAt: Long
)