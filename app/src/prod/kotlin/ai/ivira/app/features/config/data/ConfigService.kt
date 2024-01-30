package ai.ivira.app.features.config.data

import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface ConfigService {
    @GET
    suspend fun fetchTileConfigs(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<List<TileConfigItemNetwork>>>
}