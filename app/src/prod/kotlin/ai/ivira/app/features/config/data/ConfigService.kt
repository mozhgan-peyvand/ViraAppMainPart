package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.model.ConfigHamahangNetwork
import ai.ivira.app.features.config.data.model.ConfigNetwork
import ai.ivira.app.features.config.data.model.ConfigObjectNetwork
import ai.ivira.app.features.config.data.model.ConfigTileNetwork
import ai.ivira.app.features.config.data.model.ConfigVersionNetwork
import ai.ivira.app.utils.data.ViraNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface ConfigService {
    @GET
    suspend fun fetchAllConfigs(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<ConfigNetwork>>

    @GET
    suspend fun fetchLastUpdate(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<List<ConfigObjectNetwork<Long>>>>

    @GET
    suspend fun fetchTiles(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<List<ConfigObjectNetwork<ConfigTileNetwork>>>>

    @GET
    suspend fun fetchVersions(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<List<ConfigObjectNetwork<ConfigVersionNetwork>>>>

    @GET
    suspend fun fetchHamahang(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<ViraNetwork<ConfigHamahangNetwork>>
}