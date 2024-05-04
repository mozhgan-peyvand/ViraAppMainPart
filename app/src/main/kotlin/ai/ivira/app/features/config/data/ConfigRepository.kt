package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.model.ConfigObjectNetwork
import ai.ivira.app.features.config.data.model.ConfigVersionEntity
import ai.ivira.app.features.config.data.model.ConfigVersionNetwork
import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity
import ai.ivira.app.features.config.data.model.toConfigTileEntity
import ai.ivira.app.features.config.data.model.toConfigVersionEntity
import ai.ivira.app.features.config.data.model.totoConfigVersionReleaseNoteEntity
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val configRemoteDataSource: ConfigRemoteDataSource,
    private val configLocalDataSource: ConfigLocalDataSource,
    private val networkHandler: NetworkHandler
) {
    suspend fun fetchAllConfigs(): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = configRemoteDataSource.fetchAllConfigs()) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> {
                configLocalDataSource.insertConfigEntity(result.data.toConfigEntity())
                AppResult.Success(Unit)
            }
        }
    }

    // region lastUpdate
    fun getLastUpdateFetchTime() = configLocalDataSource.getLastUpdateFetchTime()

    fun updateLastUpdateFetchTime() = configLocalDataSource.updateLastUpdateFetchTime()

    fun getLastUpdate() = configLocalDataSource.getLastUpdate()

    fun insertLastUpdate(lastUpdate: Map<String, Long>) =
        configLocalDataSource.insertLastUpdate(lastUpdate)

    suspend fun fetchLastUpdate(): AppResult<Map<String, Long>> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = configRemoteDataSource.fetchLastUpdate()) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> AppResult.Success(result.data.associate { it.name to it.value })
        }
    }
    // endregion lastUpdate

    // region tiles
    fun getTiles() = configLocalDataSource.getTiles()

    suspend fun fetchTiles(): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        return when (val result = configRemoteDataSource.fetchTiles()) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> {
                configLocalDataSource.insertTiles(result.data.map { it.toConfigTileEntity() })
                AppResult.Success(Unit)
            }
        }
    }
    // endregion tiles

    // region versions
    suspend fun fetchVersions(): AppResult<Unit> {
        return when (val result = configRemoteDataSource.fetchVersions()) {
            is ApiResult.Error -> AppResult.Error(result.error.toAppException())
            is ApiResult.Success -> {
                insertVersions(result.data)
                AppResult.Success(Unit)
            }
        }
    }

    private suspend fun insertVersions(versions: List<ConfigObjectNetwork<ConfigVersionNetwork>>) {
        val newVersions = mutableListOf<ConfigVersionEntity>()
        val newReleaseNotes = mutableListOf<ConfigVersionReleaseNoteEntity>()
        versions.forEach { version ->
            newVersions.add(version.value.toConfigVersionEntity())
            newReleaseNotes.addAll(
                version.value.releaseNote.map { it.totoConfigVersionReleaseNoteEntity(version.value.versionNumber) }
            )
        }
        configLocalDataSource.insertVersions(newVersions, newReleaseNotes)
    }
    // endregion versions
}