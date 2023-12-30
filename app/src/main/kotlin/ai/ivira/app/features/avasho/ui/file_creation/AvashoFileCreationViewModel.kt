package ai.ivira.app.features.avasho.ui.file_creation

import ai.ivira.app.R
import ai.ivira.app.utils.common.safeGetInt
import android.app.Application
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// UNDO_REDO_LIMIT: Duplicate 2
private const val UNDO_REDO_LIMIT = 50
private const val AVASHO_FILE_CREATION_TOOLTIP_KEY = "avashoFileCreationTooltipKey"

@HiltViewModel
class AvashoFileCreationViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    application: Application
) : AndroidViewModel(application) {
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
            sharedPref.safeGetInt(AvashoFileCreationViewModel.KEY_DEFAULT_VOICE_NAME_COUNTER, 1)
        )
    }

    fun updateCurrentDefaultName() {
        sharedPref.edit {
            putInt(
                AvashoFileCreationViewModel.KEY_DEFAULT_VOICE_NAME_COUNTER,
                sharedPref.safeGetInt(
                    AvashoFileCreationViewModel.KEY_DEFAULT_VOICE_NAME_COUNTER,
                    1
                ) + 1
            )
        }
    }

    companion object {
        private const val KEY_DEFAULT_VOICE_NAME_COUNTER = "defaultVoiceNameCounter"
    }
}