package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.model.ConfigEntity
import ai.ivira.app.features.config.data.model.ConfigHamahangEntity
import ai.ivira.app.features.config.data.model.ConfigTileEntity
import ai.ivira.app.features.config.data.model.ConfigVersionEntity
import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity
import ai.ivira.app.utils.common.di.qualifier.ConfigSharedPref
import ai.ivira.app.utils.data.db.ViraDb
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.withTransaction
import javax.inject.Inject

private const val KEY_LAST_UPDATE_FETCH_TIME = "lastUpdateFetchTime"

class ConfigLocalDataSource @Inject constructor(
    private val db: ViraDb,
    private val dao: ConfigDao,
    @ConfigSharedPref private val prefs: SharedPreferences
) {
    suspend fun insertConfigEntity(config: ConfigEntity) {
        insertTiles(config.tiles)
        insertVersions(config.versions, config.releaseNotes)
        insertLastUpdate(config.lastUpdate)
        insertHamahangEntity(config.hamahang)
    }

    // region tiles
    fun getTiles() = dao.getTiles()

    suspend fun insertTiles(configs: List<ConfigTileEntity>) {
        db.withTransaction {
            dao.deleteTiles()
            dao.insertTiles(configs)
        }
    }
    // endregion tiles

    // region versions
    suspend fun insertVersions(
        versions: List<ConfigVersionEntity>,
        releaseNotes: List<ConfigVersionReleaseNoteEntity>
    ) {
        db.withTransaction {
            dao.deleteReleaseNotes()
            dao.deleteVersions()

            dao.insertVersions(versions)
            dao.insertReleaseNotes(releaseNotes)
        }
    }
    // endregion versions

    // region hamahang
    fun getHamahangSpeakers() = dao.getHamahangSpeakers()

    suspend fun insertHamahangEntity(hamahang: ConfigHamahangEntity) {
        db.withTransaction {
            dao.deleteHamahangSpeakers()
            dao.insertHamahangSpeakers(hamahang.speakers)
        }
    }
    // endregion hamahang

    // region LastUpdate
    fun getLastUpdateFetchTime(): Long? {
        return prefs.getLong(KEY_LAST_UPDATE_FETCH_TIME, 0L)
            .takeIf { it > 0L }
    }

    fun updateLastUpdateFetchTime() {
        prefs.edit { putLong(KEY_LAST_UPDATE_FETCH_TIME, System.currentTimeMillis()) }
    }

    fun getLastUpdate(): Map<String, Long> {
        return buildMap {
            ViraConfigs.entries.forEach { config ->
                prefs.getLong(getLastUpdateKey(config.value), 0L)
                    .takeIf { timestamp -> timestamp > 0 }
                    ?.let { timestamp -> put(config.value, timestamp) }
            }
        }
    }

    fun insertLastUpdate(lastUpdate: Map<String, Long>) {
        val supportedConfigs = ViraConfigs.entries.map { it.value }
        val newLastUpdate = lastUpdate.filterKeys { key -> key in supportedConfigs }
        if (newLastUpdate.isEmpty()) {
            prefs.edit {
                supportedConfigs.forEach { config ->
                    remove(getLastUpdateKey(config))
                }
            }
            return
        }
        prefs.edit {
            newLastUpdate.forEach { (key, value) ->
                putLong(getLastUpdateKey(key), value)
            }
        }
    }

    private fun getLastUpdateKey(config: String) = "lastUpdate_$config"
    // endregion LastUpdate
}