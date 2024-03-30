package ai.ivira.app.features.hamahang.ui.new_audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

// RecorderUtil: Duplicate 2
class HamahangRecorder constructor(
    private val context: Context,
    private val maxFileDurationInMillis: Long
) {
    companion object {
        private const val SAMPLE_RATE = 44100
        const val TAG = "RecorderTAG"
        const val RECORDING_OFFSET_MS = 1000
    }

    private val onMaxDurationReachedListeners = CopyOnWriteArrayList<OnMaxDurationReached>()

    private fun getFile(filename: String): File {
        val parent = File(File(context.filesDir, "hamahang"), "recordings")

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
            setMaxDuration((maxFileDurationInMillis - RECORDING_OFFSET_MS).toInt())
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
        }.onFailure {
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
        }.onFailure {
            Timber.tag(TAG).d(it)
        }.isSuccess
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