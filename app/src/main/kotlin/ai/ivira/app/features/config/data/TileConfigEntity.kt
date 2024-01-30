package ai.ivira.app.features.config.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TileConfigEntity(
    @PrimaryKey
    val name: String,
    val message: String,
    val status: Boolean
)