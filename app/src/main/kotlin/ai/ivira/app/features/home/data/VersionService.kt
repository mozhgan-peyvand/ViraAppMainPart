package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.GatewayTokenNetwork
import ai.ivira.app.features.home.data.entity.GetUpdateNetwork
import ai.ivira.app.features.home.data.entity.PostUpdateGatewayToken
import ai.ivira.app.features.home.data.entity.Resource
import ai.ivira.app.utils.data.api_result.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface VersionService {
    @POST
    suspend fun getUpdateGatewayToken(
        @Url url: String,
        @Body param: PostUpdateGatewayToken
    ): ApiResult<Resource<GatewayTokenNetwork>>

    @GET
    suspend fun getUpdateList(
        @Url url: String,
        @Header("gateway-token") gatewayToken: String
    ): ApiResult<Resource<GetUpdateNetwork>>
}