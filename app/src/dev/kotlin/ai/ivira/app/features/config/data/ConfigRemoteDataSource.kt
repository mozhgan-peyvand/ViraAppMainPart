package ai.ivira.app.features.config.data

import ai.ivira.app.utils.data.api_result.ApiResult
import javax.inject.Inject

class ConfigRemoteDataSource @Inject constructor(
    private val configService: ConfigService,
    private val configDataHelper: ConfigDataHelper
) {
    suspend fun fetchTileConfigs(): ApiResult<List<TileConfigItemNetwork>> {
        val result = configService.fetchTileConfigs(
            url = configDataHelper.ad(),
            user = configDataHelper.ud(),
            pass = configDataHelper.pd()
        )
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Error(result.error)
        }
    }
}