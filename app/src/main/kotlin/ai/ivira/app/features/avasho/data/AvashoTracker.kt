package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.utils.data.tracker.Tracker
import android.text.format.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvashoTracker @Inject constructor(
    private val avashoRepository: AvashoRepository
) : Tracker<AvashoTrackingFileEntity>(NO_ESTIMATE_DELAY_MS, TRACK_DELAY_AFTER_FAILURE_MS) {
    companion object {
        private const val TAG = "AvashoTracker"
        private const val NO_ESTIMATE_DELAY_MS = 30 * DateUtils.SECOND_IN_MILLIS
        private const val TRACK_DELAY_AFTER_FAILURE_MS = 15 * DateUtils.SECOND_IN_MILLIS
    }

    override val items = avashoRepository.getTrackingFiles()

    override suspend fun track(item: AvashoTrackingFileEntity): Boolean {
        return avashoRepository.trackLargeTextResult(item.token).isSuccess
    }
}