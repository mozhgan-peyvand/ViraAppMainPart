package ai.ivira.app.features.ava_negar.ui.record

import ai.ivira.app.R
import ai.ivira.app.utils.common.safeGetInt
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceRecordingViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    val recorder: Recorder,
    application: Application
) : AndroidViewModel(application) {
    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    private val _timer = MutableStateFlow(0)
    val timer: StateFlow<Int> = _timer.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000L)
                if (isActive) {
                    _timer.value += 1
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        _timer.value = 0
    }

    fun getCurrentDefaultName(): String {
        return getApplication<Application>().getString(
            R.string.lbl_default_recorded_title,
            prefs.safeGetInt(KEY_DEFAULT_NAME_COUNTER, 1)
        )
    }

    fun updateCurrentDefaultName() {
        prefs.edit {
            putInt(
                KEY_DEFAULT_NAME_COUNTER,
                prefs.safeGetInt(KEY_DEFAULT_NAME_COUNTER, 1) + 1
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        playerState.clear()
    }

    companion object {
        private const val KEY_DEFAULT_NAME_COUNTER = "defaultNameCounter"
    }
}