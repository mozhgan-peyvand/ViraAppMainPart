package ai.ivira.app.features.hamahang.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HamahangCheckingFileEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val inputFilePath: String,
    val speaker: String,
    val isProper: Boolean,
    val createdAt: Long
)