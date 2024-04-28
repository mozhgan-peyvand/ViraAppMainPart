package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.features.login.data.LoginRepository
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.sms_retriever.ViraGoogleSmsRetriever
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginMobileViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val uiException: UiException,
    private val viraGoogleSmsRetriever: ViraGoogleSmsRetriever
) : ViewModel() {
    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState = _uiViewState.asSharedFlow()

    val isRequestAllowed = _uiViewState.mapLatest { it != UiLoading } // TODO: Add any other situation to be handled here (such as rateLimit)

    val phoneNumber = TextFieldState(initialText = "")

    private val _loginRequiredIsShown = mutableStateOf(false)
    val loginRequiredIsShown: State<Boolean> = _loginRequiredIsShown

    init {
        viewModelScope.launch {
            _loginRequiredIsShown.value = repository.getLoginRequiredIsShown()
        }
    }

    fun setLoginRequiredShowed() {
        repository.saveLoginRequiredIsShown(true)
        _loginRequiredIsShown.value = true
    }

    fun sendOTP() {
        viewModelScope.launch(IO) {
            if (!phoneNumberIsValid()) {
                _uiViewState.emit(
                    UiError(
                        message = uiException.getErrorInvalidPhoneNumber(),
                        isSnack = false
                    )
                )
                return@launch
            }

            _uiViewState.emit(UiLoading)

            viraGoogleSmsRetriever.startService()

            viewModelScope.launch(IO) {
                when (
                    val result = repository.sendOtp(phoneNumber = phoneNumber.text.toString())
                ) {
                    is AppResult.Error -> _uiViewState.emit(
                        UiError(message = uiException.getErrorMessage(result.error))
                    )
                    is AppResult.Success -> _uiViewState.emit(UiSuccess)
                }
            }
        }
    }

    private fun phoneNumberIsValid(): Boolean {
        phoneNumber.text.apply {
            return !(isBlank() || length != 11 || !startsWith("09"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viraGoogleSmsRetriever.stopService()
    }
}