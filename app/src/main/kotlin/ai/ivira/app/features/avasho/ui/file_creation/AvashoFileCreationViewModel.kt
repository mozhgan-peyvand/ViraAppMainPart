package ai.ivira.app.features.avasho.ui.file_creation

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvashoFileCreationViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    private var _textBody = mutableStateOf("")
    var textBody = _textBody

    fun addTextToList(value: String) {
        _textBody.value = (value)
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
}