package ai.ivira.app.features.home.data
import ai.ivira.app.features.home.data.entity.SettingNetwork
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.ApiResult.Error
import ai.ivira.app.utils.data.api_result.ApiResult.Success
import javax.inject.Inject

class VersionRemoteDataSource @Inject constructor(
    private val versionService: VersionService,
    private val helper: VersionDataHelper
) {
    suspend fun getUpdateVersionList(): ApiResult<List<SettingNetwork>> {
        return when (val result = versionService.getUpdateList(
            url = helper.up(),
            gatewaySystem = helper.gwu(),
        )) {
            is Success -> Success(result.data.data.versions)
            is Error -> Error(result.error)
        }
    }
}