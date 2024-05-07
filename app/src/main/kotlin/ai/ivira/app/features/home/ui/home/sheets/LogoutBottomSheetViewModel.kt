package ai.ivira.app.features.home.ui.home.sheets

import ai.ivira.app.features.login.data.LoginRepository
import ai.ivira.app.utils.data.api_result.AppResult.Error
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutBottomSheetViewModel @Inject constructor(
    private val uiException: UiException,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState = _uiViewState.asSharedFlow()

    val isUserLogin = loginRepository.tokenFlow()
        .map { TokenStatus.Value(it) }
        .stateIn(TokenStatus.Idle)

    private var logoutJob: Job? = null

    fun logout() {
        logoutJob?.cancel()
        logoutJob = viewModelScope.launch {
            _uiViewState.emit(UiLoading)
            when (val result = loginRepository.logout()) {
                is Success -> {
                    _uiViewState.emit(UiSuccess)
                }
                is Error -> {
                    _uiViewState.emit(
                        UiError(
                            message = uiException.getErrorMessage(result.error),
                            isSnack = true
                        )
                    )
                }
            }
        }
    }

    fun resetLogoutRequest() {
        logoutJob?.cancel()
        logoutJob = null
    }

    sealed interface TokenStatus {
        data object Idle : TokenStatus
        data class Value(val token: String?) : TokenStatus
    }
}