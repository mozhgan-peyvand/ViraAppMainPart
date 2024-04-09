package ai.ivira.app.features.hamahang.ui.new_audio

import android.content.Context
import android.text.format.DateUtils
import kotlinx.coroutines.CoroutineScope
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
import java.io.File

class VoiceRecorderState(
    context: Context,
    val maxFileDurationInMillis: Long,
    private val coroutineScope: CoroutineScope,
    private val onMaxDurationReached: (File) -> Unit
) : HamahangRecorder.OnMaxDurationReached {
    private val _timer = MutableStateFlow(0L)
    val timer: StateFlow<Int> = _timer.map { (it / 1000).toInt() }
        .distinctUntilChanged()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private var timerJob: Job? = null

    val recorder: HamahangRecorder = HamahangRecorder(
        context = context,
        maxFileDurationInMillis = maxFileDurationInMillis
    )

    init {
        recorder.addOnMaxDurationReachedListener(this)
    }

    fun getRecordedFile() = File(recorder.currentFile?.absolutePath.orEmpty())

    fun clear() {
        resetTimer()
        recorder.stop()
        recorder.removeOnMaxDurationReachedListener(this)
    }

    fun startTimer() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(TIMER_DELAY_MS)
                if (isActive) {
                    _timer.value += TIMER_DELAY_MS
                }
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        _timer.value = 0
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun maxFileReached() {
        pauseTimer()
        onMaxDurationReached(getRecordedFile())
    }

    companion object {
        private const val TIMER_DELAY_MS = 1 * DateUtils.SECOND_IN_MILLIS
    }
}