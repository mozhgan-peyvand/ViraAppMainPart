package ai.ivira.app.features.ava_negar.ui.record

import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewModel.Companion.MAX_FILE_DURATION_MS
import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewModel.Companion.RECORDING_OFFSET_MS
import ai.ivira.app.utils.common.ifFailure
import android.content.Context
import android.media.MediaRecorder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class Recorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val SAMPLE_RATE = 44100
        const val TAG = "RecorderTAG"
    }

    private val onMaxDurationReachedListeners = CopyOnWriteArrayList<OnMaxDurationReached>()

    private fun getFile(filename: String): File {
        val parent = File(File(context.filesDir, "avanegar"), "recordings")

        if (!parent.exists()) {
            parent.mkdirs()
        }
        val file = File(parent, "$filename.mp3")
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    private var mediaRecorder: MediaRecorder? = null
    private val _currentFile = AtomicReference<File?>(null)
    val currentFile: File? get() = _currentFile.get()

    private fun createMediaRecorder(): MediaRecorder {
        return if (VERSION.SDK_INT >= VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    fun start(name: String): Boolean {
        mediaRecorder = createMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setAudioSamplingRate(SAMPLE_RATE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setMaxDuration((MAX_FILE_DURATION_MS - RECORDING_OFFSET_MS).toInt())
            setOnInfoListener { _, what, _ ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stop()
                    onMaxDurationReachedListeners.forEach(OnMaxDurationReached::maxFileReached)
                }
            }
            setOutputFile(
                FileOutputStream(
                    getFile(name).also { _currentFile.set(it) }
                ).fd
            )
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        }

        return kotlin.runCatching {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        }.ifFailure {
            Timber.tag(TAG).d(it)
        }.isSuccess
    }

    fun pause(): Boolean {
        return kotlin.runCatching {
            val recorder = mediaRecorder ?: return false
            if (isPauseResumeSupported()) {
                recorder.pause()
            }
        }.ifFailure {
            Timber.tag(TAG).d(it)
        }.isSuccess
    }

    fun resume(): Boolean {
        return kotlin.runCatching {
            val recorder = mediaRecorder ?: return false
            if (isPauseResumeSupported()) {
                recorder.resume()
            }
        }.ifFailure {
            Timber.tag(TAG).d(it)
        }.isSuccess
    }

    fun stop(): Boolean {
        return kotlin.runCatching {
            val recorder = mediaRecorder ?: return false
            recorder.stop()
            recorder.reset()
            recorder.release()
            mediaRecorder = null
        }.ifFailure {
            Timber.tag(TAG).d(it)
        }.isSuccess
    }

    fun removeCurrentRecording(): Boolean {
        val file = _currentFile.get() ?: return false
        return file.delete()
    }

    fun isPauseResumeSupported(): Boolean {
        return VERSION.SDK_INT >= VERSION_CODES.N
    }

    fun addOnMaxDurationReachedListener(listener: OnMaxDurationReached) {
        onMaxDurationReachedListeners.add(listener)
    }

    fun removeOnMaxDurationReachedListener(listener: OnMaxDurationReached) {
        onMaxDurationReachedListeners.remove(listener)
    }

    interface OnMaxDurationReached {
        fun maxFileReached()
    }
}