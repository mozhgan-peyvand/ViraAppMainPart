package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import ai.ivira.app.utils.data.tracker.Tracker
import android.text.format.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HamahangTracker @Inject constructor(
    private val hamahangRepository: HamahangRepository
) : Tracker<HamahangTrackingFileEntity>(NO_ESTIMATE_DELAY_MS, TRACK_DELAY_AFTER_FAILURE_MS) {
    companion object {
        private const val TAG = "HamahangTracker"
        private const val NO_ESTIMATE_DELAY_MS = 30 * DateUtils.SECOND_IN_MILLIS
        private const val TRACK_DELAY_AFTER_FAILURE_MS = 10 * DateUtils.SECOND_IN_MILLIS
    }

    override val items = hamahangRepository.getTrackingFiles()

    override suspend fun track(item: HamahangTrackingFileEntity): Boolean {
        return hamahangRepository.trackVoiceConversion(item.token).isSuccess
    }
}