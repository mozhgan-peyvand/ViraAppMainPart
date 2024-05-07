package ai.ivira.app.features.avasho.ui.file_creation

import ai.ivira.app.R
import ai.ivira.app.features.avasho.data.AvashoRepository
import ai.ivira.app.utils.common.safeGetInt
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// UNDO_REDO_LIMIT: Duplicate 2
private const val UNDO_REDO_LIMIT = 50
private const val AVASHO_FILE_CREATION_TOOLTIP_KEY = "avashoFileCreationTooltipKey"

@HiltViewModel
class AvashoFileCreationViewModel @Inject constructor(
    private val repository: AvashoRepository,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    application: Application
) : AndroidViewModel(application) {
    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private var textList = mutableListOf("")
    private val currentIndex = mutableIntStateOf(textList.size - 1)

    private val _shouldShowTooltip = mutableStateOf(shouldShowTooltip())
    val shouldShowTooltip: State<Boolean>
        get() = _shouldShowTooltip

    private var _textBody = mutableStateOf("")
    var textBody = _textBody

    fun addTextToList(value: String) {
        if (textList.size >= UNDO_REDO_LIMIT) {
            textList.removeAt(0)
        }
        textList.add(value)
        _textBody.value = (value)
        currentIndex.intValue = textList.size - 1
    }

    fun appendToText(newText: String) {
        _textBody.value += newText
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
    }

    fun undo() {
        if (currentIndex.intValue > 0) {
            currentIndex.intValue -= 1
            _textBody.value = textList[currentIndex.intValue]
        }
    }

    fun redo() {
        if (textList.size - 1 > currentIndex.intValue) {
            currentIndex.intValue += 1
            _textBody.value = textList[currentIndex.intValue]
        }
    }

    fun canRedo(): Boolean {
        return textList.size - 1 > currentIndex.intValue
    }

    fun canUndo(): Boolean {
        return currentIndex.intValue > 0
    }

    private fun shouldShowTooltip(): Boolean {
        return sharedPref.getBoolean(AVASHO_FILE_CREATION_TOOLTIP_KEY, true)
    }

    fun doNotShowTooltipAgain() {
        viewModelScope.launch {
            _shouldShowTooltip.value = false
            sharedPref.edit().putBoolean(AVASHO_FILE_CREATION_TOOLTIP_KEY, false).apply()
        }
    }

    fun getCurrentDefaultName(): String {
        return getApplication<Application>().getString(
            R.string.lbl_default_voice_title,
            sharedPref.safeGetInt(KEY_DEFAULT_VOICE_NAME_COUNTER, 1)
        )
    }

    fun updateCurrentDefaultName() {
        sharedPref.edit {
            putInt(
                KEY_DEFAULT_VOICE_NAME_COUNTER,
                sharedPref.safeGetInt(
                    KEY_DEFAULT_VOICE_NAME_COUNTER,
                    1
                ) + 1
            )
        }
    }

    fun checkSpeech(speech: String, onSuccess: () -> Unit) {
        viewModelScope.launch(IO) {
            _uiViewState.emit(UiLoading)

            when (val result = repository.checkSpeech(speech)) {
                is AppResult.Error -> {
                    _uiViewState.emit(
                        UiError(
                            message = uiException.getAvashoErrorMessageRequestFailedTryAgainLater(),
                            isSnack = true
                        )
                    )
                }

                is AppResult.Success -> {
                    if (result.data) {
                        _uiViewState.emit(
                            UiError(
                                message = uiException.getAvashoErrorMessageTextContainsInappropriateWords(),
                                isSnack = true
                            )
                        )
                    } else {
                        _uiViewState.emit(UiSuccess)
                        onSuccess()
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_DEFAULT_VOICE_NAME_COUNTER = "defaultVoiceNameCounter"
    }
}