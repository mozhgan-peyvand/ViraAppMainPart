package ai.ivira.app.features.config.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigVersionEntity(
    @PrimaryKey
    val versionNumber: Int,
    val isForce: Boolean,
    val versionName: String
)

fun ConfigVersionNetwork.toConfigVersionEntity(): ConfigVersionEntity {
    return ConfigVersionEntity(
        versionNumber = versionNumber,
        isForce = isForce == 1,
        versionName = versionName
    )
}