package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.hamahang.ui.new_audio.components.HamahangAudioBoxMode
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.stateIn
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.format.DateUtils
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HamahangNewAudioViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val uiException: UiException
) : ViewModel() {
    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private val _mode = MutableStateFlow<HamahangAudioBoxMode>(HamahangAudioBoxMode.Idle)
    val mode = _mode.asStateFlow()

    val speakers: List<HamahangSpeaker> = HamahangSpeaker.values().asList()
    private val _selectedSpeaker = MutableStateFlow<HamahangSpeaker?>(null)
    val selectedSpeaker = _selectedSpeaker.asStateFlow()

    val isOkToGenerate = combine(_mode, _selectedSpeaker) { mode, selectedSpeaker ->
        mode is HamahangAudioBoxMode.Preview && selectedSpeaker != null
    }.stateIn(false)

    private lateinit var retriever: MediaMetadataRetriever

    init {
        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }
    }

    fun changeSpeaker(speaker: HamahangSpeaker) {
        _selectedSpeaker.value =
            if (selectedSpeaker.value == speaker) null else speaker
    }

    fun startRecording() {
        _mode.value = HamahangAudioBoxMode.Recording
    }

    fun stopRecording() {
        _mode.value = HamahangAudioBoxMode.Preview(File(""))
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }

    suspend fun checkIfUriDurationIsOk(context: Context, uri: Uri?): Boolean {
        if (uri == null) {
            _uiViewState.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
            return false
        }

        val duration = getFileDuration(context, uri)
        if (duration <= 0L) {
            _uiViewState.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
            return false
        }

        // Duplicate 1: check file durations
        if (duration > MAX_FILE_DURATION_MS) {
            _uiViewState.emit(
                UiError(
                    uiException.getErrorMessageMaxLengthExceeded((MAX_FILE_DURATION_MS / DateUtils.MINUTE_IN_MILLIS).toInt()),
                    isSnack = true
                )
            )
            return false
        }

        return true
    }

    @WorkerThread
    private fun getFileDuration(context: Context, uri: Uri?): Long {
        if (!this::retriever.isInitialized) return 0L

        return kotlin.runCatching {
            retriever.setDataSource(context, uri)
            val time: String? =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong()
        }.onFailure { if (BuildConfig.DEBUG) it.printStackTrace() }.getOrNull().orZero()
    }

    override fun onCleared() {
        kotlin.runCatching {
            if (this::retriever.isInitialized) {
                retriever.release()
            }
        }
    }

    fun setUploadedFile(filePath: String?) {
        filePath?.let {
            _mode.value = HamahangAudioBoxMode.Preview(File(it))
        }
    }

    companion object {
        const val MAX_FILE_DURATION_MS = 2 * DateUtils.MINUTE_IN_MILLIS
    }
}
