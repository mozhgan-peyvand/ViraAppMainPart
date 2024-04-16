package ai.ivira.app.features.login.ui.mobile

import ai.ivira.app.features.login.data.LoginRepository
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginMobileViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val uiException: UiException
) : ViewModel() {
    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    val requestAllowed = _uiViewState.map { it != UiLoading } // TODO: Add any other situation to be handled here (such as rateLimit)
        .stateIn(false)

    fun changePhoneNumber(value: String) {
        _phoneNumber.value = value.filter { it.isDigit() }
            .take(11)
    }

    fun sendOTP() {
        if (!phoneNumberIsValid()) {
            _uiViewState.update {
                UiError(
                    message = uiException.getErrorInvalidPhoneNumber(),
                    isSnack = false
                )
            }
            return
        }

        _uiViewState.update { UiLoading }
        viewModelScope.launch(IO) {
            when (
                val result = repository.sendOtp(phoneNumber = _phoneNumber.value)
            ) {
                is AppResult.Error -> _uiViewState.update {
                    UiError(message = uiException.getErrorMessage(result.error))
                }
                is AppResult.Success -> _uiViewState.update { UiSuccess }
            }
        }
    }

    private fun phoneNumberIsValid(): Boolean {
        _phoneNumber.value.apply {
            return !(isBlank() || length != 11 || !startsWith("09"))
        }
    }

    fun clearUiState() {
        _uiViewState.value = UiIdle
    }
}