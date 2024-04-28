package ai.ivira.app.features.login.ui.otp

import ai.ivira.app.R
import ai.ivira.app.features.login.data.LoginRepository
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.sms_retriever.ViraGoogleSmsRetriever
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginOtpViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val uiException: UiException,
    private val application: Application,
    private val viraGoogleSmsRetriever: ViraGoogleSmsRetriever,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val mobile = savedStateHandle.get<String>("mobile").orEmpty()

    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    private val _resendOtpViewState = MutableSharedFlow<UiStatus>()
    val resendOtpViewState = _resendOtpViewState.asSharedFlow()

    private var resendOtoJob: Job? = null

    var otpTextValue by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            viraGoogleSmsRetriever.smsResult.collect { smsResult ->
                when (smsResult) {
                    is ViraGoogleSmsRetriever.SmsResult.Message -> {
                        otpTextValue = smsResult.code
                    }
                    is ViraGoogleSmsRetriever.SmsResult.ConsentIntent -> {
                        // TODO Implement it
                    }
                }
            }
        }
    }

    fun changeOtp(value: String) {
        otpTextValue = value.filter { it.isDigit() }
            .take(5)
    }

    fun resendOtp() {
        if (resendOtoJob != null) return
        resendOtoJob = viewModelScope.launch {
            _resendOtpViewState.emit(UiLoading)

            viraGoogleSmsRetriever.startService()

            when (val result = repository.sendOtp(mobile)) {
                is AppResult.Error -> {
                    _resendOtpViewState.emit(UiError(message = uiException.getErrorMessage(result.error)))
                }
                is AppResult.Success -> {
                    _resendOtpViewState.emit(UiSuccess)
                }
            }
            resendOtoJob = null
        }
    }

    fun verifyOtpRequest() {
        val validationError = otpValidation(otpTextValue)
        if (validationError != null) {
            _uiViewState.update { UiError(message = validationError) }
            return
        }

        _uiViewState.update { UiLoading }

        viewModelScope.launch(IO) {
            when (val result = repository.verifyOtp(mobile, otpTextValue)) {
                is AppResult.Success -> {
                    _uiViewState.update { UiSuccess }
                }
                is AppResult.Error -> {
                    _uiViewState.update {
                        UiError(message = uiException.getErrorMessage(result.error))
                    }
                }
            }
        }
    }

    private fun otpValidation(otpCode: String): String? {
        return when {
            otpCode.isEmpty() -> application.getString(R.string.msg_otp_not_enter)
            otpCode.length < 5 -> application.getString(R.string.msg_otp_not_is_enter_wrong)
            else -> {
                null
            }
        }
    }

    fun clearUiState() {
        _uiViewState.value = UiIdle
    }

    override fun onCleared() {
        super.onCleared()
        viraGoogleSmsRetriever.stopService()
    }
}