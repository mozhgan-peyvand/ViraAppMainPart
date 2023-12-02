package ai.ivira.app.features.home.data

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.features.home.data.entity.VersionEntity
import ai.ivira.app.utils.common.di.qualifier.EncryptedSharedPref
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.ui.ApiErrorCodes.InvalidToken
import ai.ivira.app.utils.ui.ApiErrorCodes.TokenNotProvided
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val UPDATE_GATEWAY_TOKEN = "updateGatewayToken"
private const val CHECK_UPDATE_PERIODICALLY_KEY = "checkUpdatePeriodicallyKey"
private const val LAST_UPDATE_CHECK = "lastUpdateCheck"
private const val SHOWING_UPDATE_LATER_INTERVAL = 48 * DateUtils.HOUR_IN_MILLIS
private const val CHECK_UPDATE_PERIODICALLY_INTERVAL = 48 * DateUtils.HOUR_IN_MILLIS

@Singleton
class VersionRepository @Inject constructor(
    private val versionLocalDataSource: VersionLocalDataSource,
    private val versionRemoteDataSource: VersionRemoteDataSource,
    private val sharedPref: SharedPreferences,
    @EncryptedSharedPref private val encryptedSharedPref: SharedPreferences,
    private val networkHandler: NetworkHandler
) {
    init {
        CoroutineScope(IO).launch {
            if (shouldShowBottomSheet()) {
                getChangeLogFromRemote()
            }
        }
    }

    // when update checked automatically each 48 hours
    private fun updateChecked() {
        sharedPref.edit {
            this.putLong(LAST_UPDATE_CHECK, System.currentTimeMillis())
        }
    }

    // show update later when click on later button
    fun showUpdateBottomSheetLater() {
        sharedPref.edit {
            this.putLong(CHECK_UPDATE_PERIODICALLY_KEY, System.currentTimeMillis())
        }
    }

    fun shouldShowBottomSheet(): Boolean {
        val shouldShowUpdateBottomSheet = sharedPref.getLong(
            CHECK_UPDATE_PERIODICALLY_KEY,
            System.currentTimeMillis()
        )

        val showUpdateBottomSheetAgain =
            shouldShowUpdateBottomSheet + SHOWING_UPDATE_LATER_INTERVAL < System.currentTimeMillis()

        val previousUpdateShown = sharedPref.getLong(LAST_UPDATE_CHECK, System.currentTimeMillis())
        val hasEnoughTimePassedToShowUpdate =
            previousUpdateShown + CHECK_UPDATE_PERIODICALLY_INTERVAL < System.currentTimeMillis()

        return showUpdateBottomSheetAgain || hasEnoughTimePassedToShowUpdate
    }

    private suspend fun getUpdateGatewayToken(): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            when (val result = versionRemoteDataSource.getUpdateGatewayToken().toAppResult()) {
                is Success -> {
                    encryptedSharedPref.edit {
                        this.putString(UPDATE_GATEWAY_TOKEN, result.data)
                    }

                    Success(result.data)
                }

                is AppResult.Error -> {
                    AppResult.Error(result.error)
                }
            }
        } else {
            AppResult.Error(AppException.NetworkConnectionException())
        }
    }

    suspend fun getChangeLogFromRemote(): AppResult<Unit> {
        return if (networkHandler.hasNetworkConnection()) {
            when (
                val result = versionRemoteDataSource.getUpdateVersionList(gatewayToken())
                    .toAppResult()
            ) {
                is Success -> {
                    val data = result.data.map { settingNetwork ->
                        settingNetwork.toVersionEntity()
                    }

                    insertChangeLogToDatabase(data)

                    versionLocalDataSource.deleteReleaseNote()
                    result.data.map { settingNetwork ->
                        insertReleaseNoteToDatabase(settingNetwork.value.releaseNote.map {
                            it.toReleaseNoteEntity(settingNetwork.value.versionNumber)
                        })
                    }
                    updateChecked()
                    Success(Unit)
                }

                is AppResult.Error -> {
                    if (result.error is AppException.RemoteDataSourceException && (
                            result.error.body.contains(TokenNotProvided.value) ||
                                result.error.body.contains(InvalidToken.value)
                            )
                    ) {
                        when (getUpdateGatewayToken()) {
                            is Success -> {
                                val newResult = versionRemoteDataSource.getUpdateVersionList(
                                    gatewayToken()
                                ).toAppResult()

                                when (newResult) {
                                    is Success -> {
                                        val data = newResult.data.map { settingNetwork ->
                                            settingNetwork.toVersionEntity()
                                        }

                                        insertChangeLogToDatabase(data)

                                        versionLocalDataSource.deleteReleaseNote()
                                        newResult.data.map { settingNetwork ->
                                            insertReleaseNoteToDatabase(settingNetwork.value.releaseNote.map {
                                                it.toReleaseNoteEntity(settingNetwork.value.versionNumber)
                                            })
                                        }

                                        updateChecked()
                                        Success(Unit)
                                    }
                                    is AppResult.Error -> AppResult.Error(result.error)
                                }
                            }

                            is AppResult.Error -> {
                                AppResult.Error(result.error)
                            }
                        }
                    } else {
                        AppResult.Error(result.error)
                    }
                }
            }
        } else {
            AppResult.Error(AppException.NetworkConnectionException())
        }
    }

    fun getChangeLogFromLocal(): Flow<List<VersionDto>> {
        return versionLocalDataSource.getChangeLog().map { list ->
            val currentVersionIndex = list.indexOfFirst { versionDto ->
                versionDto.versionEntity.versionNumber == BuildConfig.VERSION_CODE
            }

            if (currentVersionIndex == -1 || currentVersionIndex + 1 == list.size) {
                listOf()
            } else {
                list.subList(currentVersionIndex + 1, list.size).filter { versionDto ->
                    versionDto.releaseNotes.isNotEmpty()
                }.reversed()
            }
        }
    }

    private suspend fun insertChangeLogToDatabase(list: List<VersionEntity>) {
        versionLocalDataSource.insertChangeLog(list)
    }

    private suspend fun insertReleaseNoteToDatabase(list: List<ReleaseNoteEntity>) {
        versionLocalDataSource.insertReleaseNote(list)
    }

    private fun gatewayToken() = encryptedSharedPref.getString(UPDATE_GATEWAY_TOKEN, "").orEmpty()
}