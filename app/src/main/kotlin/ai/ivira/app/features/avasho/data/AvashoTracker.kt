package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.utils.data.api_result.AppResult
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import saman.zamani.persiandate.PersianDate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvashoTracker @Inject constructor(private val avashoRepository: AvashoRepository) {
    companion object {
        private const val TAG = "AvashoTracker"
        private const val NO_ESTIMATE_DELAY_MS = 30 * DateUtils.SECOND_IN_MILLIS
    }

    private val coroutineScope = ProcessLifecycleOwner.get().lifecycleScope
    private val currentlyTracking = ConcurrentHashMap<String, Job>()
    private val finished = CopyOnWriteArraySet<String>()
    private val mutex = Mutex()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            avashoRepository.getTrackingFiles()
                .distinctUntilChanged()
                .collect { files ->
                    if (files.isEmpty()) {
                        // remove everything in tracking list if existing...
                        if (currentlyTracking.isNotEmpty()) {
                            mutex.withLock {
                                currentlyTracking.values.forEach { job ->
                                    job.cancel()
                                }
                                currentlyTracking.clear()
                            }
                        }
                        return@collect
                    }
                    mutex.withLock {
                        val tokens = files
                            .filterNot { file -> file.token in finished }
                            .map { file -> file.token }
                            .toSet()
                        val removed = currentlyTracking.keys - tokens
                        val added = tokens - currentlyTracking.keys

                        removed.forEach { token ->
                            currentlyTracking.remove(token)?.cancel()
                        }

                        files.filter { it.token in added }
                            .forEach { file ->
                                currentlyTracking[file.token] = coroutineScope.launch(Dispatchers.IO) {
                                    trackFile(file)
                                }
                            }
                    }
                }
        }

        // TODO: implement delete files!
    }

    private suspend fun trackFile(tracking: AvashoTrackingFileEntity) {
        delay(500) // safety check whether it is removed or not!
        if (tracking.token !in currentlyTracking.keys) {
            return
        }

        val job = currentlyTracking[tracking.token] ?: return
        if (tracking.processEstimation == null) {
            // using fallback mode
            trackFileFallbackMode(job, tracking)
            return
        }
        trackFileWithEstimation(job, tracking)
    }

    private suspend fun trackFileFallbackMode(
        job: Job,
        tracking: AvashoTrackingFileEntity,
        initialDelayMs: Long = 0
    ) {
        delay(if (initialDelayMs > 0) initialDelayMs else NO_ESTIMATE_DELAY_MS)
        while (true) {
            if (job.isCancelled || !job.isActive || job.isCompleted) break

            val isSuccess = avashoRepository.trackLargeTextResult(tracking.token) is AppResult.Success
            if (isSuccess) {
                mutex.withLock {
                    currentlyTracking.remove(tracking.token)
                    finished.add(tracking.token)
                }
                break
            }
            delay(NO_ESTIMATE_DELAY_MS)
        }
    }

    // region trackFile with estimation
    private suspend fun trackFileWithEstimation(job: Job, tracking: AvashoTrackingFileEntity) {
        if (job.isCancelled || !job.isActive || job.isCompleted) return
        if (tracking.processEstimation == null) {
            trackFileFallbackMode(job, tracking)
            return
        }

        val bootDiffMs = SystemClock.elapsedRealtime() - tracking.insertAt.bootTime
        if (bootDiffMs > 0) {
            trackFileWithDelay(job, tracking, bootDiffMs, tracking.processEstimation)
            return
        }
        val systemDiffMs = PersianDate().time - tracking.insertAt.systemTime
        if (systemDiffMs > 0) {
            trackFileWithDelay(job, tracking, systemDiffMs, tracking.processEstimation)
            return
        }

        trackFileFallbackMode(job, tracking)
    }

    private suspend fun trackFileWithDelay(
        job: Job,
        tracking: AvashoTrackingFileEntity,
        diffMs: Long,
        estimation: Int
    ) {
        val diffSecond = diffMs / 1000

        if (diffSecond < estimation) {
            delay((estimation - diffSecond) * 1000)
            if (job.isCancelled || !job.isActive || job.isCompleted) return

            val isSuccess = avashoRepository.trackLargeTextResult(tracking.token) is AppResult.Success
            if (isSuccess) {
                mutex.withLock {
                    currentlyTracking.remove(tracking.token)
                    finished.add(tracking.token)
                }
            }
            return
        }

        // Need to account for failure time!!
        if (tracking.lastFailure != null) {
            val bootTimeDiffMs = SystemClock.elapsedRealtime() - tracking.lastFailure.bootTime
            if (bootTimeDiffMs in 1 until NO_ESTIMATE_DELAY_MS) {
                val initialDelayMs = NO_ESTIMATE_DELAY_MS - bootTimeDiffMs
                trackFileFallbackMode(job, tracking, initialDelayMs = initialDelayMs)
                return
            }

            val systemTimeDiffMs = PersianDate().time - tracking.lastFailure.systemTime
            if (systemTimeDiffMs in 1 until NO_ESTIMATE_DELAY_MS) {
                val initialDelayMs = NO_ESTIMATE_DELAY_MS - systemTimeDiffMs
                trackFileFallbackMode(job, tracking, initialDelayMs = initialDelayMs)
                return
            }
        }
        trackFileFallbackMode(job, tracking)
    }
    // endregion trackFile with estimation
}