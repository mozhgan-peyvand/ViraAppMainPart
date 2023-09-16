package ir.part.app.intelligentassistant.features.ava_negar.ui.record

import android.app.Application
import android.media.MediaPlayer
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.common.ifFailure
import kotlinx.coroutines.Dispatchers
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

    var isPlaying by mutableStateOf(false)
        private set

    var progress by mutableFloatStateOf(0f)
        private set

    val duration: Int
        get() = mediaPlayer.duration

    init {
        mediaPlayer.setOnCompletionListener {
            kotlin.runCatching {
                isPlaying = false
                progress = 0f
            }
        }
    }

    fun tryInitWith(file: File?) {
        if (file == null || !file.exists()) return

        if (file.absolutePath != currentFile?.absolutePath) {
            if (currentFile != null) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
            currentFile = file
            mediaPlayer.setDataSource(application.applicationContext, file.toUri())
            mediaPlayer.prepare()
        }
    }

    fun startPlaying() {
        kotlin.runCatching {
            mediaPlayer.start()
            isPlaying = true
            playJob?.cancel()
            playJob = ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
                while (isPlaying && isActive) {
                    progress = currentPosition / 1000.0f
                    delay(1000)
                }
            }
        }.ifFailure {
            Toast.makeText(
                application.applicationContext,
                R.string.mgs_general_error_playing_file,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun seekTo(position: Float) {
        kotlin.runCatching {
            progress = position
            mediaPlayer.seekTo((position * 1000).toInt())
        }
    }

    fun stopPlaying() {
        kotlin.runCatching {
            isPlaying = false
            mediaPlayer.pause()
        }
    }

    fun reset() {
        playJob?.cancel()
        progress = 0f
        isPlaying = false
    }

    fun clear() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}