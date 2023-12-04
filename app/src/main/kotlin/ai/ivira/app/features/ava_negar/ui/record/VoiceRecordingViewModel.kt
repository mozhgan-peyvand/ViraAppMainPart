package ai.ivira.app.features.ava_negar.ui.record

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.Recorder.OnMaxDurationReached
import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewState.Idle
import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewState.Stopped
import ai.ivira.app.utils.common.safeGetInt
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.text.format.DateUtils
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceRecordingViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    val recorder: Recorder,
    application: Application
) : AndroidViewModel(application), OnMaxDurationReached {
    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    private val _timer = MutableStateFlow(0L)
    val timer: StateFlow<Int> = _timer.map { (it / 1000).toInt() }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val state: MutableState<VoiceRecordingViewState> = mutableStateOf(Idle)

    private var timerJob: Job? = null

    init {
        recorder.addOnMaxDurationReachedListener(this)
    }

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(TIMER_DELAY_MS)
                if (isActive) {
                    _timer.value += TIMER_DELAY_MS
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

    // when this is called, we are sure that the mediaRecorder is stopped
    override fun maxFileReached() {
        pauseTimer()
        state.value = Stopped(showPreview = true)
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        playerState.clear()
        recorder.removeOnMaxDurationReachedListener(this)
    }

    companion object {
        private const val KEY_DEFAULT_NAME_COUNTER = "defaultNameCounter"
        private const val TIMER_DELAY_MS = 50L

        // note: 1h is shown in ui, if changed, change that as well
        const val MAX_FILE_DURATION_MS = 60 * DateUtils.MINUTE_IN_MILLIS
        const val RECORDING_OFFSET_MS = 300
    }
}