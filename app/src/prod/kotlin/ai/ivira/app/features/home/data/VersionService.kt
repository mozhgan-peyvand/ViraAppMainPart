package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.GetUpdateNetwork
import ai.ivira.app.features.home.data.entity.Resource
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface VersionService {
    @GET
    suspend fun getUpdateList(
        @Url url: String,
        @Header("gateway-system") gatewaySystem: String
    ): ApiResult<Resource<GetUpdateNetwork>>
}