package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.PostUpdateGatewayToken
import ai.ivira.app.features.home.data.entity.SettingNetwork
import ai.ivira.app.features.home.data.entity.UpdateGatewayAuthenticationPack
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import javax.inject.Inject

class VersionRemoteDataSource @Inject constructor(
    private val versionService: VersionService,
    private val helper: VersionDataHelper
) {
    init {
        System.loadLibrary("vira")
    }

    suspend fun getUpdateGatewayToken(): ApiResult<String> {
        val body = PostUpdateGatewayToken(
            system = helper.gws(),
            authenticationPack = UpdateGatewayAuthenticationPack(
                username = helper.gwu(),
                password = helper.gwp()
            )
        )

        val result = versionService.getUpdateGatewayToken(
            url = helper.gw(),
            param = body
        )

        return when (result) {
            is Success -> Success(result.data.data.token)
            is Error -> Error(result.error)
        }
    }

    suspend fun getUpdateVersionList(token: String): ApiResult<List<SettingNetwork>> {
        val result = versionService.getUpdateList(
            url = helper.up(),
            gatewayToken = token
        )

        return when (result) {
            is Success -> Success(result.data.data.versions)
            is Error -> Error(result.error)
        }
    }
}