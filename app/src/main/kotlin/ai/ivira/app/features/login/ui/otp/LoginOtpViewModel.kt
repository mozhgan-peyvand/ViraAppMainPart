package ai.ivira.app.features.login.ui.otp

import ai.ivira.app.R
import ai.ivira.app.features.login.data.LoginRepository
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.ApiErrorCodes
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.combine
import ai.ivira.app.utils.ui.sms_retriever.ViraGoogleSmsRetriever
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState = _uiViewState.asSharedFlow()
    private val uiViewStateAsStateFlow = uiViewState.stateIn(UiIdle)

    private val _resendOtpViewState = MutableSharedFlow<UiStatus>()
    val resendOtpViewState = _resendOtpViewState.asSharedFlow()

    private var resendOtpJob: Job? = null

    private val _otpIsInvalid = mutableStateOf(false)
    val otpIsInvalid: State<Boolean> = _otpIsInvalid

    var otpTextValue by mutableStateOf("")
        private set

    var isRequestAllowed = combine(
        snapshotFlow { otpTextValue },
        uiViewStateAsStateFlow
    ) { code, uiState ->
        !(uiState == UiLoading || code.length < OTP_SIZE)
    }.stateIn(false)

    init {
        viewModelScope.launch {
            viraGoogleSmsRetriever.smsResult.collect { smsResult ->
                when (smsResult) {
                    is ViraGoogleSmsRetriever.SmsResult.Message -> {
                        if (uiViewState.stateIn(UiIdle).value != UiLoading) {
                            changeOtp(smsResult.code)
                        }
                    }
                    is ViraGoogleSmsRetriever.SmsResult.ConsentIntent -> {
                        // TODO Implement it
                    }
                }
            }
        }

        // this is used to automatically send the verifyOtpRequest on user filling the box
        viewModelScope.launch {
            snapshotFlow { otpTextValue }
                .distinctUntilChanged()
                .map {
                    if (uiViewStateAsStateFlow.value == UiLoading) return@map
                    if (it.length < OTP_SIZE) return@map
                    verifyOtpRequest()
                }.launchIn(this)
        }
    }

    fun changeOtp(value: String) {
        val newValue = value.filter { it.isDigit() }.take(OTP_SIZE)
        if (newValue != otpTextValue) {
            _otpIsInvalid.value = false
        }
        otpTextValue = newValue
    }

    fun resendOtp() {
        if (resendOtpJob != null) return
        resendOtpJob = viewModelScope.launch {
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
            resendOtpJob = null
        }
    }

    private fun checkInvalidOtpError(exception: AppException) {
        if (exception is AppException.RemoteDataSourceException &&
            exception.body == ApiErrorCodes.InvalidOtp.value
        ) {
            _otpIsInvalid.value = true
        }
    }

    fun verifyOtpRequest() {
        viewModelScope.launch(IO) {
            val validationError = otpValidation(otpTextValue)
            if (validationError != null) {
                _uiViewState.emit(UiError(message = validationError))
                return@launch
            }

            _uiViewState.emit(UiLoading)

            when (val result = repository.verifyOtp(mobile, otpTextValue)) {
                is AppResult.Success -> {
                    _uiViewState.emit(UiSuccess)
                }
                is AppResult.Error -> {
                    checkInvalidOtpError(result.error)
                    _uiViewState.emit(
                        UiError(
                            message = uiException.getErrorMessage(result.error),
                            isSnack = !_otpIsInvalid.value
                        )
                    )
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

    override fun onCleared() {
        super.onCleared()
        viraGoogleSmsRetriever.stopService()
    }

    companion object {
        const val OTP_SIZE = 5
    }
}