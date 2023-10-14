package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import android.os.SystemClock
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvanegarTracker @Inject constructor(
    private val avanegarRepository: AvanegarRepository
) {
    private val isLock = AtomicBoolean(false)
    private val trackingJob = AtomicReference<Job?>(null)
    private val trackingRequestJobs = ConcurrentHashMap<String, Boolean>()

    init {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            avanegarRepository.getTrackingFiles().collect { files ->
                if (files.isNotEmpty() && trackingJob.get() == null) {
                    trackingJob.set(
                        launch {
                            startTracking()
                            trackingJob.set(null)
                            isLock.set(false)
                        }
                    )
                }
            }
        }
    }

    private suspend fun startTracking() {
        if (isLock.get()) return
        isLock.set(true)

        var loopCount = 0

        while (true) {
            if (!currentCoroutineContext().isActive) {
                break
            }
            val files = avanegarRepository.getTrackingFilesSync()
            if (files.isEmpty()) {
                log("no files, terminating tracker!")
                break
            }

            files.forEach loop@{ tracking ->
                val currentBootTime = SystemClock.elapsedRealtime()
                val timePersian = PersianDate().time
                log("--------------------------------")
                log(
                    JSONObject().apply {
                        put("trackingItem", getTrackingAsJsonString(tracking))
                        put("loopCount", loopCount)
                        put("currentBootElapsed", currentBootTime)
                        put("currentTimestamp", timePersian)
                    }.toString(2)
                )
                if (tracking.processEstimation == null || tracking.lastFailure != null) {
                    if (loopCount >= 6) { // around 30 seconds
                        log("No processEstimate, count right sending request")
                        sendTrackingRequest(tracking.token)
                    } else {
                        log("No processEstimate, loopCount=$loopCount")
                    }
                    return@loop
                }

                if (currentBootTime > tracking.bootElapsedTime) {
                    val diff = (currentBootTime - tracking.bootElapsedTime) / 1000
                    if (tracking.processEstimation <= diff) {
                        log("bootTime more, sending request")
                        sendTrackingRequest(tracking.token)
                    } else {
                        log("bootTime more but not enough: $diff")
                    }
                } else {
                    if (timePersian > tracking.createdAt) {
                        val diff = (timePersian - tracking.createdAt) / 1000
                        if (tracking.processEstimation <= diff) {
                            log("time more sending request")
                            sendTrackingRequest(tracking.token)
                        } else {
                            log("time more but not enough: $diff")
                        }
                    } else {
                        if (loopCount >= 6) { // around 30 seconds
                            log("with process but count right sending request")
                            sendTrackingRequest(tracking.token)
                        } else {
                            log("with process but count is not right $loopCount")
                        }
                    }
                }
            }
            delay(5_000)
            loopCount++
            if (loopCount > 6) {
                loopCount = 0
            }
        }

        isLock.set(false)
    }

    private fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Timber.tag("AvanegarTracker").v(message)
        }
    }

    private fun getTrackingAsJsonString(tracking: AvanegarTrackingFileEntity): JSONObject {
        return JSONObject().also { item ->
            item.put("token", tracking.token)
            item.put("title", tracking.title)
            item.put("processEstimation", tracking.processEstimation)
            item.put(
                "creationTime",
                JSONObject().also { created ->
                    created.put("timestamp", tracking.createdAt)
                    created.put("bootElapsedTime", tracking.bootElapsedTime)
                }
            )
            item.put(
                "lastFailure",
                tracking.lastFailure?.let {
                    JSONObject().also { lastFailure ->
                        lastFailure.put(
                            "timestamp",
                            tracking.lastFailure.lastFailedRequest
                        )
                        lastFailure.put(
                            "bootElapsedTime",
                            tracking.lastFailure.lastTrackedBootElapsed
                        )
                    }
                }
            )
        }
    }

    private fun sendTrackingRequest(token: String) {
        if (trackingRequestJobs.contains(token)) return

        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            delay(50) // just to make sure it is added in map first
            avanegarRepository.trackLargeFileResult(token)
            trackingRequestJobs.remove(token)
        }

        trackingRequestJobs[token] = true
    }
}