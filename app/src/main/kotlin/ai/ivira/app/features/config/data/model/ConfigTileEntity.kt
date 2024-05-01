package ai.ivira.app.features.config.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigTileEntity(
    @PrimaryKey
    val name: String,
    val message: String,
    val status: Boolean
)

fun ConfigObjectNetwork<ConfigTileNetwork>.toConfigTileEntity(): ConfigTileEntity {
    return ConfigTileEntity(
        name = name,
        message = value.message,
        status = value.status
    )
}