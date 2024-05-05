package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.model.ConfigHamahangNetwork
import ai.ivira.app.features.config.data.model.ConfigNetwork
import ai.ivira.app.features.config.data.model.ConfigObjectNetwork
import ai.ivira.app.features.config.data.model.ConfigTileNetwork
import ai.ivira.app.features.config.data.model.ConfigVersionNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import javax.inject.Inject

class ConfigRemoteDataSource @Inject constructor(
    private val configService: ConfigService,
    private val configDataHelper: ConfigDataHelper
) {
    suspend fun fetchAllConfigs(): ApiResult<ConfigNetwork> {
        return when (
            val result = configService.fetchAllConfigs(
                url = configDataHelper.barjavandBaseUrl,
                gatewaySystem = configDataHelper.gp()
            )
        ) {
            is ApiResult.Error -> ApiResult.Error(result.error)
            is ApiResult.Success -> ApiResult.Success(result.data.data)
        }
    }

    suspend fun fetchLastUpdate(): ApiResult<List<ConfigObjectNetwork<Long>>> {
        return when (
            val result = configService.fetchLastUpdate(
                url = configDataHelper.lastUpdateUrl,
                gatewaySystem = configDataHelper.gp()
            )
        ) {
            is ApiResult.Error -> ApiResult.Error(result.error)
            is ApiResult.Success -> ApiResult.Success(result.data.data)
        }
    }

    suspend fun fetchTiles(): ApiResult<List<ConfigObjectNetwork<ConfigTileNetwork>>> {
        val result = configService.fetchTiles(
            url = configDataHelper.tilesUrl,
            gatewaySystem = configDataHelper.gp()
        )
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun fetchVersions(): ApiResult<List<ConfigObjectNetwork<ConfigVersionNetwork>>> {
        val result = configService.fetchVersions(
            url = configDataHelper.versionsUrl,
            gatewaySystem = configDataHelper.gp()
        )
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }

    suspend fun fetchHamahang(): ApiResult<ConfigHamahangNetwork> {
        val result = configService.fetchHamahang(
            url = configDataHelper.hamahangUrl,
            gatewaySystem = configDataHelper.gp()
        )
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }
}