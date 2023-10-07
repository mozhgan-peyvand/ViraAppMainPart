package ai.ivira.app.features.ava_negar.data

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        while (true) {
            val files = avanegarRepository.getTrackingFilesSync()
            if (files.isEmpty()) break
            files.forEach {
                avanegarRepository.trackLargeFileResult(it.token)
            }
            delay(30_000)
        }

        isLock.set(false)
    }
}