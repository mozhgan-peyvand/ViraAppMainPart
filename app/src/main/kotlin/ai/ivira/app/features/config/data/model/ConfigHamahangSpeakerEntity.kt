package ai.ivira.app.features.config.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigHamahangSpeakerEntity(
    @PrimaryKey
    val name: String,
    val status: Boolean
)

fun ConfigObjectNetwork<ConfigHamahangSpeakerNetwork>.toConfigHamahangSpeakerEntity(): ConfigHamahangSpeakerEntity {
    return ConfigHamahangSpeakerEntity(
        name = name,
        status = value.status
    )
}