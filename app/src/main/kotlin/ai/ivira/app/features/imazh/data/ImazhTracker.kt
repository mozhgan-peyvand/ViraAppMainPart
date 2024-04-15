package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import ai.ivira.app.utils.data.tracker.Tracker
import android.text.format.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImazhTracker @Inject constructor(
    private val imazhRepository: ImazhRepository
) : Tracker<ImazhTrackingFileEntity>(NO_ESTIMATE_DELAY_MS, TRACK_DELAY_AFTER_FAILURE_MS) {
    companion object {
        private const val TAG = "ImazhTracker"
        private const val NO_ESTIMATE_DELAY_MS = 10 * DateUtils.SECOND_IN_MILLIS
        private const val TRACK_DELAY_AFTER_FAILURE_MS = 10 * DateUtils.SECOND_IN_MILLIS
    }

    override val items = imazhRepository.getTrackingFiles()

    override suspend fun track(item: ImazhTrackingFileEntity): Boolean {
        return imazhRepository.trackImageResult(item.token).isSuccess
    }
}