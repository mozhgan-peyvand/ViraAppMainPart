package ai.ivira.app.features.config.data.model

data class ConfigEntity(
    val lastUpdate: Map<String, Long>,
    val versions: List<ConfigVersionEntity>,
    val releaseNotes: List<ConfigVersionReleaseNoteEntity>,
    val tiles: List<ConfigTileEntity>
)