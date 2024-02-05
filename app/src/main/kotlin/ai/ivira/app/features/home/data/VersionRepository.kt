package ai.ivira.app.features.home.data

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.home.data.entity.ChangelogEntity
import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.SettingNetwork
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.features.home.data.entity.VersionEntity
import ai.ivira.app.utils.data.JsonHelper
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.data.api_result.toAppResult
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val CHECK_UPDATE_PERIODICALLY_KEY = "checkUpdatePeriodicallyKey"
private const val LAST_UPDATE_CHECK = "lastUpdateCheck"
private const val SHOWING_UPDATE_LATER_INTERVAL = 48 * DateUtils.HOUR_IN_MILLIS
private const val CHECK_UPDATE_PERIODICALLY_INTERVAL = 48 * DateUtils.HOUR_IN_MILLIS
const val CURRENT_CHANGELOG_VERSION_KEY = "currentChangelogVersionKey"

@Singleton
class VersionRepository @Inject constructor(
    private val versionLocalDataSource: VersionLocalDataSource,
    private val versionRemoteDataSource: VersionRemoteDataSource,
    private val sharedPref: SharedPreferences,
    private val jsonHelper: JsonHelper,
    private val networkHandler: NetworkHandler
) {
    private val changelogVersion: Int = sharedPref.getInt(CURRENT_CHANGELOG_VERSION_KEY, 0)

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

    suspend fun getChangeLogFromRemote(): AppResult<Unit> {
        return if (networkHandler.hasNetworkConnection()) {
            when (
                val result = versionRemoteDataSource.getUpdateVersionList().toAppResult()
            ) {
                is Success -> {
                    insertChangeLogToDatabase(result.data)
                    updateChecked()
                    Success(Unit)
                }

                is AppResult.Error -> {
                    AppResult.Error(result.error)
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

    private suspend fun insertChangeLogToDatabase(settingNetwork: List<SettingNetwork>) {
        val versions = mutableListOf<VersionEntity>()
        val releaseNotes = mutableListOf<ReleaseNoteEntity>()

        settingNetwork.forEach { setting ->
            versions.add(setting.toVersionEntity())
            releaseNotes.addAll(
                setting.value.releaseNote.map {
                    it.toReleaseNoteEntity(setting.value.versionNumber)
                }
            )
        }
        versionLocalDataSource.insertChangeLog(
            versions = versions,
            releaseNotes = releaseNotes
        )
    }

    fun getChangelog(): Flow<List<ChangelogEntity>> {
        return flow {
            val poetListJson = jsonHelper.openJsonFromAssets("change_log.json") ?: return@flow
            val list = jsonHelper.getList<ChangelogEntity>(poetListJson) ?: return@flow

            emit(list.filter { it.versionCode > changelogVersion })
        }
    }
}