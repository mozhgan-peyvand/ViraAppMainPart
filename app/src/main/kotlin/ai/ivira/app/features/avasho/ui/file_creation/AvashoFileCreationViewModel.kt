package ai.ivira.app.features.avasho.ui.file_creation

import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// UNDO_REDO_LIMIT: Duplicate 2
private const val UNDO_REDO_LIMIT = 50

@HiltViewModel
class AvashoFileCreationViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    private var textList = mutableListOf("")
    private val currentIndex = mutableIntStateOf(textList.size - 1)

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
}