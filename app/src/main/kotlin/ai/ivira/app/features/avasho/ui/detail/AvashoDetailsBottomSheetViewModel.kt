package ai.ivira.app.features.avasho.ui.detail

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.ui.StorageUtils
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiStatus
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AvashoDetailsBottomSheetViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val storageUtils: StorageUtils,
    private val fileOperationHelper: FileOperationHelper,
    private val application: Application
) : AndroidViewModel(application) {
    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    fun saveToDownloadFolder(filePath: String, fileName: String): Boolean {
        if (storageUtils.getAvailableSpace() <= File(filePath).length()) {
            viewModelScope.launch {
                _uiViewState.emit(
                    UiError(application.getString(R.string.msg_not_enough_space), true)
                )
            }
            return false
        }

        return fileOperationHelper.copyFileToDownloadFolder(
            filePath = filePath,
            fileName = fileName
        )
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
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

    override fun onCleared() {
        super.onCleared()
        playerState.clear()
    }
}