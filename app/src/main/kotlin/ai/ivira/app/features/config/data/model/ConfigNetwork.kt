package ai.ivira.app.features.config.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfigNetwork(
    val versions: List<ConfigObjectNetwork<ConfigVersionNetwork>>,
    val lastUpdate: List<ConfigObjectNetwork<Long>>,
    val tiles: List<ConfigObjectNetwork<ConfigTileNetwork>>
) {
    fun toConfigEntity(): ConfigEntity {
        val newVersions = mutableListOf<ConfigVersionEntity>()
        val newReleaseNotes = mutableListOf<ConfigVersionReleaseNoteEntity>()

        versions.forEach { version ->
            newVersions.add(version.value.toConfigVersionEntity())
            newReleaseNotes.addAll(
                version.value.releaseNote.map {
                    it.totoConfigVersionReleaseNoteEntity(version.value.versionNumber)
                }
            )
        }

        return ConfigEntity(
            lastUpdate = lastUpdate.associate { it.name to it.value },
            versions = newVersions,
            releaseNotes = newReleaseNotes,
            tiles = tiles.map { it.toConfigTileEntity() }
        )
    }
}