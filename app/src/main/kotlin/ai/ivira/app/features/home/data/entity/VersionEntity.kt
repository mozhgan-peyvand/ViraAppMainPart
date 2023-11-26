package ai.ivira.app.features.home.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VersionEntity(
    @PrimaryKey
    val versionNumber: Int,
    val name: String,
    val isForce: Boolean,
    val versionName: String
)