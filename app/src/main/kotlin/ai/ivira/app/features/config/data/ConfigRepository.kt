package ai.ivira.app.features.config.data

import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import android.content.SharedPreferences
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    private val configRemoteDataSource: ConfigRemoteDataSource,
    private val configLocalDataSource: ConfigLocalDataSource,
    private val sharedPref: SharedPreferences
) {
    suspend fun fetchTileConfigs(): AppResult<List<TileConfigItemNetwork>> {
        val result = configRemoteDataSource.fetchTileConfigs().toAppResult()
        when (result) {
            is AppResult.Error -> Unit
            is AppResult.Success -> {
                updateLastTileConfigTime(System.currentTimeMillis())
                configLocalDataSource.insertTileConfigs(result.data.map { it.toTileConfigEntity() })
            }
        }
        return result
    }

    fun getTileConfigs() = configLocalDataSource.getTileConfigs()

    private fun updateLastTileConfigTime(time: Long) {
        sharedPref.edit().putLong(LAST_TILE_CONFIG_TIME, time).apply()
    }

    fun getLastTileConfigTime(): Long {
        return sharedPref.getLong(LAST_TILE_CONFIG_TIME, -1)
    }

    companion object {
        private const val LAST_TILE_CONFIG_TIME = "last_tile_config_time"
    }
}