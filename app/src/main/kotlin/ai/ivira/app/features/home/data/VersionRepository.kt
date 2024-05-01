package ai.ivira.app.features.home.data

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.home.data.entity.ChangelogEntity
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.utils.data.JsonHelper
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
    private val sharedPref: SharedPreferences,
    private val jsonHelper: JsonHelper
) {
    private val changelogVersion: Int = sharedPref.getInt(CURRENT_CHANGELOG_VERSION_KEY, 0)

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
        var shouldShowUpdateBottomSheet = sharedPref.getLong(
            CHECK_UPDATE_PERIODICALLY_KEY,
            -1
        )

        if (shouldShowUpdateBottomSheet == -1L) {
            sharedPref.edit()
                .putLong(CHECK_UPDATE_PERIODICALLY_KEY, System.currentTimeMillis())
                .apply()
            shouldShowUpdateBottomSheet = System.currentTimeMillis()
        }

        val showUpdateBottomSheetAgain =
            shouldShowUpdateBottomSheet + SHOWING_UPDATE_LATER_INTERVAL < System.currentTimeMillis()

        val previousUpdateShown = sharedPref.getLong(LAST_UPDATE_CHECK, System.currentTimeMillis())
        val hasEnoughTimePassedToShowUpdate =
            previousUpdateShown + CHECK_UPDATE_PERIODICALLY_INTERVAL < System.currentTimeMillis()

        return showUpdateBottomSheetAgain || hasEnoughTimePassedToShowUpdate
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

    fun getChangelog(): Flow<List<ChangelogEntity>> {
        return flow {
            val poetListJson = jsonHelper.openJsonFromAssets("change_log.json") ?: return@flow
            val list = jsonHelper.getList<ChangelogEntity>(poetListJson) ?: return@flow

            emit(list.filter { it.versionCode > changelogVersion })
        }
    }
}