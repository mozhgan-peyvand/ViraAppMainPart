package ai.ivira.app.features.ava_negar.ui.record

import ai.ivira.app.R
import android.app.Application
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class VoicePlayerState(
    private val mediaPlayer: MediaPlayer,
    private val application: Application
) {
    private var currentFile: File? = null
    private var playJob: Job? = null
    private val currentPosition: Int get() = mediaPlayer.currentPosition

    var duration by mutableIntStateOf(0)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var progress by mutableFloatStateOf(0f)
        private set

    var remainingTime by mutableIntStateOf(0)
        private set

    init {
        mediaPlayer.setOnPreparedListener { mp ->
            duration = mp.duration.coerceAtLeast(0)
            remainingTime = duration
        }
        mediaPlayer.setOnCompletionListener {
            kotlin.runCatching {
                isPlaying = false
                remainingTime = duration
                progress = 0f
            }
        }
    }

    fun tryInitWith(file: File?, forcePrepare: Boolean = false): Boolean {
        if (file == null || !file.exists()) return false

        return kotlin.runCatching {
            if (forcePrepare || file.absolutePath != currentFile?.absolutePath) {
                if (currentFile != null) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }
                currentFile = file
                mediaPlayer.setDataSource(application.applicationContext, file.toUri())
                mediaPlayer.prepare()
            }
        }.isSuccess
    }

    fun startPlaying() {
        kotlin.runCatching {
            mediaPlayer.start()
            isPlaying = true
            playJob?.cancel()
            playJob = ProcessLifecycleOwner.get().lifecycleScope.launch(IO) {
                while (isPlaying && isActive) {
                    kotlin.runCatching {
                        progress = currentPosition / 1000.0f
                        delay(1000)
                        remainingTime = duration - currentPosition
                    }
                }
            }
        }.onFailure {
            Toast.makeText(
                application.applicationContext,
                R.string.mgs_general_error_playing_file,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun seekTo(position: Float) {
        kotlin.runCatching {
            val newTime = (position * 1000).toInt()
            progress = position
            remainingTime = duration - newTime
            mediaPlayer.seekTo(newTime)
        }
    }

    fun stopPlaying() {
        kotlin.runCatching {
            isPlaying = false
            playJob?.cancel()
            playJob = null
            mediaPlayer.pause()
        }
    }

    fun reset() {
        playJob?.cancel()
        progress = 0f
        isPlaying = false
        remainingTime = duration
    }

    fun clear() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun stopMediaPlayer() {
        kotlin.runCatching {
            mediaPlayer.stop()
        }
    }

    fun seekForward() {
        val newTime = (currentPosition + SEEK_TIME_MS).coerceAtMost(duration)
        remainingTime = (duration - newTime).coerceAtLeast(0)
        if (remainingTime <= 0) {
            mediaPlayer.seekTo(0)
            mediaPlayer.pause()
            reset()
        } else {
            mediaPlayer.seekTo(newTime)
            progress = newTime / 1000.0f
        }
    }

    fun seekBackward() {
        val newTime = (currentPosition - SEEK_TIME_MS).coerceAtLeast(0)
        remainingTime = (duration - newTime).coerceIn(0, duration)
        mediaPlayer.seekTo(newTime)
        progress = newTime / 1000.0f
    }

    companion object {
        const val SEEK_TIME_MS = 10000
    }
}