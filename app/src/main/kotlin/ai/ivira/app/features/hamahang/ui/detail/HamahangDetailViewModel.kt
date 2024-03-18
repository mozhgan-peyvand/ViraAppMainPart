package ai.ivira.app.features.hamahang.ui.detail

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.hamahang.data.HamahangRepository
import ai.ivira.app.features.hamahang.ui.archive.model.toHamahangProcessedFileView
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.ui.StorageUtils
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.shareMultipleImage
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HamahangDetailViewModel @Inject constructor(
    private val application: Application,
    private val sharedPref: SharedPreferences,
    private val storageUtils: StorageUtils,
    private val fileOperationHelper: FileOperationHelper,
    private val repository: HamahangRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val processedFile = savedStateHandle.getStateFlow("id", -1)
        .flatMapLatest { id ->
            if (id == -1) {
                flowOf(null)
            } else {
                repository.getProcessedFiles(id).map { value ->
                    value?.toHamahangProcessedFileView()
                }
            }
        }.stateIn(initial = null)

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    val filePath get() = processedFile.value?.filePath

    fun removeAudio(id: Int, filePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProcessedFile(id, filePath)
        }
    }

    fun shareItem(context: Context) {
        filePath?.let { filePath ->
            if (File(filePath).exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    File(filePath)
                )
                shareMultipleImage(context, arrayListOf(uri))
            }
        }
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
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

    fun saveItemToDownloadFolder(): Boolean {
        filePath?.let { filePath ->
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
                fileName = File(filePath).nameWithoutExtension
            )
        } ?: return false
    }

    override fun onCleared() {
        super.onCleared()
        playerState.clear()
    }
}