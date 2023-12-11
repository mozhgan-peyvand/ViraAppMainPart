package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvanegarTracker @Inject constructor(
    private val avanegarRepository: AvanegarRepository,
    @ApplicationContext private val context: Context
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

        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            val files = avanegarRepository.getAllFilePaths().associateBy { it.filePath }
            File(context.filesDir, "avanegar").walkTopDown().filter {
                it.isFile && it.extension == "mp3" && !files.contains(it.absolutePath)
            }.forEach {
                kotlin.runCatching {
                    val deleted = it.delete()
                    log("Removing: ${it.absolutePath} -> $deleted")
                }
            }
        }
    }

    private suspend fun startTracking() {
        if (isLock.get()) return
        isLock.set(true)

        // This logic is wrong, must be reworked (the loopCount)
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

                if (currentBootTime > tracking.insertAt.bootTime) {
                    val diff = (currentBootTime - tracking.insertAt.bootTime) / 1000
                    if (tracking.processEstimation <= diff) {
                        log("bootTime more, sending request")
                        sendTrackingRequest(tracking.token)
                    } else {
                        log("bootTime more but not enough: $diff")
                    }
                } else {
                    if (timePersian > tracking.insertAt.systemTime) {
                        val diff = (timePersian - tracking.insertAt.systemTime) / 1000
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
                    created.put("timestamp", tracking.insertAt.systemTime)
                    created.put("bootElapsedTime", tracking.insertAt.bootTime)
                }
            )
            item.put(
                "lastFailure",
                tracking.lastFailure?.let {
                    JSONObject().also { lastFailure ->
                        lastFailure.put(
                            "timestamp",
                            tracking.lastFailure.systemTime
                        )
                        lastFailure.put(
                            "bootElapsedTime",
                            tracking.lastFailure.bootTime
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