package ai.ivira.app.features.config.data

import javax.inject.Inject

class ConfigLocalDataSource @Inject constructor(
    private val configDao: ConfigDao
) {
    fun getTileConfigs() = configDao.getTileConfigs()

    suspend fun insertTileConfigs(configs: List<TileConfigEntity>) =
        configDao.insertTileConfigs(configs)
}