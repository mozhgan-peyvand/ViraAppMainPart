package ai.ivira.app.features.imazh.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImazhHistoryEntity(
    @PrimaryKey
    val prompt: String,
    val createdAt: Long
)