package ai.ivira.app.utils.data.tracker

import android.os.SystemClock
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import saman.zamani.persiandate.PersianDate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

abstract class Tracker<T : TrackableItem>(
    private val delayNoEstimateMs: Long,
    private val delayEstimationPassedMs: Long = delayNoEstimateMs,
    private val coroutineScope: CoroutineScope = ProcessLifecycleOwner.get().lifecycleScope
) {
    abstract val items: Flow<List<T>>

    private val currentlyTracking = ConcurrentHashMap<String, Job>()
    private val finished = CopyOnWriteArraySet<String>()
    private val mutex = Mutex()

    private var initJob: Job? = null

    fun init() {
        if (initJob != null) return

        initJob = coroutineScope.launch(IO) {
            items.distinctUntilChanged()
                .collect { files ->
                    if (files.isEmpty()) {
                        clearCurrentTracking()
                        return@collect
                    }
                    mutex.withLock {
                        val tokens = files
                            .filterNot { file -> file.trackingToken in finished }
                            .map { file -> file.trackingToken }
                            .toSet()
                        val removed = currentlyTracking.keys - tokens
                        val added = tokens - currentlyTracking.keys

                        removed.forEach { token ->
                            currentlyTracking.remove(token)?.cancel()
                        }

                        files.filter { it.trackingToken in added }
                            .forEach { file ->
                                currentlyTracking[file.trackingToken] = coroutineScope.launch {
                                    startTracking(file)
                                }
                            }
                    }
                }
        }
        initJob?.invokeOnCompletion { initJob = null }
    }

    private suspend fun clearCurrentTracking() {
        if (currentlyTracking.isEmpty()) return
        mutex.withLock {
            currentlyTracking.values.forEach { job -> job.cancel() }
            currentlyTracking.clear()
        }
    }

    private suspend fun startTracking(tracking: T) {
        delay(500) // safety check whether it is removed or not!
        if (tracking.trackingToken !in currentlyTracking.keys) {
            return
        }

        val job = currentlyTracking[tracking.trackingToken] ?: return
        if (tracking.trackingProcessEstimation == null) {
            // using fallback mode
            trackFileFallbackMode(job, tracking)
            return
        }
        trackFileWithEstimation(job, tracking)
    }

    private suspend fun trackFileFallbackMode(
        job: Job,
        tracking: T,
        initialDelayMs: Long = 0,
        loopDelay: Long = delayNoEstimateMs
    ) {
        delay(if (initialDelayMs > 0) initialDelayMs else delayNoEstimateMs)
        while (true) {
            if (job.isCancelled || !job.isActive || job.isCompleted) break

            if (track(tracking)) {
                mutex.withLock {
                    currentlyTracking.remove(tracking.trackingToken)
                    finished.add(tracking.trackingToken)
                }
                break
            }
            delay(loopDelay)
        }
    }

    private suspend fun trackFileWithEstimation(job: Job, tracking: T) {
        if (job.isCancelled || !job.isActive || job.isCompleted) return
        val processEstimation = tracking.trackingProcessEstimation ?: return trackFileFallbackMode(
            job,
            tracking
        )

        val bootDiffMs = SystemClock.elapsedRealtime() - tracking.trackingInsertAt.bootTime
        if (bootDiffMs > 0) {
            trackFileWithDelay(job, tracking, bootDiffMs, processEstimation)
            return
        }
        val systemDiffMs = PersianDate().time - tracking.trackingInsertAt.systemTime
        if (systemDiffMs > 0) {
            trackFileWithDelay(job, tracking, systemDiffMs, processEstimation)
            return
        }

        trackFileFallbackMode(job = job, tracking = tracking, loopDelay = delayEstimationPassedMs)
    }

    private suspend fun trackFileWithDelay(job: Job, tracking: T, diffMs: Long, estimation: Int) {
        val diffSecond = diffMs / 1000

        if (diffSecond < estimation) {
            delay((estimation - diffSecond) * 1000)
            if (job.isCancelled || !job.isActive || job.isCompleted) return

            if (track(tracking)) {
                mutex.withLock {
                    currentlyTracking.remove(tracking.trackingToken)
                    finished.add(tracking.trackingToken)
                }
            } else {
                trackFileFallbackMode(
                    job = job,
                    tracking = tracking,
                    loopDelay = delayEstimationPassedMs
                )
            }
            return
        }

        // Need to account for failure time!!
        val lastFailure = tracking.trackingLastFailure
        if (lastFailure != null) {
            val bootTimeDiffMs = SystemClock.elapsedRealtime() - lastFailure.bootTime
            if (bootTimeDiffMs in 1 until delayNoEstimateMs) {
                val initialDelayMs = delayNoEstimateMs - bootTimeDiffMs
                trackFileFallbackMode(
                    job,
                    tracking,
                    initialDelayMs = initialDelayMs,
                    loopDelay = delayEstimationPassedMs
                )
                return
            }

            val systemTimeDiffMs = PersianDate().time - lastFailure.systemTime
            if (systemTimeDiffMs in 1 until delayNoEstimateMs) {
                val initialDelayMs = delayNoEstimateMs - systemTimeDiffMs
                trackFileFallbackMode(job, tracking, initialDelayMs, delayEstimationPassedMs)
                return
            }
        }
        trackFileFallbackMode(job, tracking, 10, delayEstimationPassedMs)
    }

    /**
     * This is called when it is time to track the item.
     * @param item item for which tracking request must be sent
     * @return true if item was ready, and false if item is not yet ready
     */
    abstract suspend fun track(item: T): Boolean
}