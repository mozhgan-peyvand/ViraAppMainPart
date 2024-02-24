package ai.ivira.app.features.imazh.ui.details

import ai.ivira.app.R
import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.ui.StorageUtils
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.shareMultipleImage
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImazhDetailsViewModel @Inject constructor(
    private val application: Application,
    private val imazhRepository: ImazhRepository,
    private val sharedPref: SharedPreferences,
    private val storageUtils: StorageUtils,
    private val fileOperationHelper: FileOperationHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val archiveFile = savedStateHandle.getStateFlow("id", -1)
        .flatMapLatest { id ->
            if (id == -1) {
                flowOf(null)
            } else {
                imazhRepository.getPhotoInfo(id).map { imazhProcessedEntity ->
                    imazhProcessedEntity?.toImazhProcessedFileView()
                }
            }
        }.stateIn(initial = null)

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    val filePath get() = archiveFile.value?.filePath

    fun removeImage(id: Int, imagePath: String) {
        viewModelScope.launch(IO) {
            imazhRepository.deleteProcessedFile(id)
            runCatching {
                File(imagePath).delete()
            }
        }
    }

    fun getImageBuilder(
        imageBuilder: ImageRequest.Builder,
        urlPath: String
    ): ImageRequest.Builder {
        return imageBuilder
            .setHeader("ApiKey", imazhRepository.sai())
            .data(imazhRepository.bi() + urlPath)
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
}