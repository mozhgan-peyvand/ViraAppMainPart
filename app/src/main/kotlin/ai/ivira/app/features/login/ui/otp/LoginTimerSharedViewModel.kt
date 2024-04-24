package ai.ivira.app.features.login.ui.otp

import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpTimerSharedViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    private val _timerState = MutableStateFlow<LoginTimerState>(LoginTimerState.NotStart)
    val timerState = _timerState.asStateFlow()

    private var timberJob: Job? = null

    fun startTimer() {
        timberJob?.cancel()
        timberJob = viewModelScope.launch {
            otpCounter().collect {
                _timerState.value = it
            }
        }
    }

    fun checkTimerFromSharePref() {
        viewModelScope.launch {
            val time = countingRemainedTimeFromSharedPreferences()
            if (time > 0L) {
                otpCounter(time).collect {
                    _timerState.value = it
                }
            }
        }
    }

    private fun otpCounter(start: Long = TIMER_DURATION, end: Long = 0): Flow<LoginTimerState> {
        return flow<LoginTimerState> {
            var i = start
            while (i > end) {
                emit(LoginTimerState.Start(i))
                delay(1_000)
                i -= 1000
            }
        }.onCompletion {
            emit(LoginTimerState.End)
        }.flowOn(IO)
    }

    fun saveTimerToSharePref() {
        val value = _timerState.value
        if (value is LoginTimerState.Start) {
            sharedPref.edit().apply {
                putLong(
                    TIME_LEFT_KEY_PREFERENCES,
                    (_timerState.value as LoginTimerState.Start).currentTime
                )
                putLong(STOP_TIMER_KEY_PREFERENCES, System.currentTimeMillis())
            }.apply()
        }
    }

    private fun countingRemainedTimeFromSharedPreferences(): Long {
        val timesLeft = sharedPref.getLong(TIME_LEFT_KEY_PREFERENCES, 0)
        val stopTime: Long = sharedPref.getLong(STOP_TIMER_KEY_PREFERENCES, 0)
        val currentTime = System.currentTimeMillis()
        val timeSpentInBackground = (currentTime - stopTime)
        return timesLeft - timeSpentInBackground
    }

    companion object {
        const val STOP_TIMER_KEY_PREFERENCES = "stopTimer"
        const val TIME_LEFT_KEY_PREFERENCES = "timeLeft"
        const val TIMER_DURATION = 2 * DateUtils.MINUTE_IN_MILLIS
    }
}

sealed interface LoginTimerState {
    data object NotStart : LoginTimerState
    data class Start(val currentTime: Long) : LoginTimerState
    data object End : LoginTimerState
}