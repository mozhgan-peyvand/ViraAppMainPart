package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangSpeakerView
import ai.ivira.app.features.hamahang.ui.new_audio.components.HamahangAudioBoxMode
import ai.ivira.app.utils.common.file.FileCache
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.common.safeGetInt
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.core.net.toFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val fileCache: FileCache,
    private val uiException: UiException,
    application: Application
) : AndroidViewModel(application) {
    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private val _mode = MutableStateFlow<HamahangAudioBoxMode>(HamahangAudioBoxMode.Idle)
    val mode = _mode.asStateFlow()

    val speakers: List<HamahangSpeakerView> = HamahangSpeakerView.values().asList()
    private val _selectedSpeaker = MutableStateFlow<HamahangSpeakerView?>(null)
    val selectedSpeaker = _selectedSpeaker.asStateFlow()

    val isOkToGenerate = combine(_mode, _selectedSpeaker) { mode, selectedSpeaker ->
        mode is HamahangAudioBoxMode.Preview && selectedSpeaker != null
    }.stateIn(false)

    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    private lateinit var retriever: MediaMetadataRetriever

    val voiceRecorderState = VoiceRecorderState(
        context = application,
        maxFileDurationInMillis = MAX_FILE_DURATION_MS,
        coroutineScope = viewModelScope
    ) {
        stopRecording {}
    }

    init {
        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }
    }

    fun changeSpeaker(speaker: HamahangSpeakerView) {
        _selectedSpeaker.value =
            if (selectedSpeaker.value == speaker) null else speaker
    }

    fun startRecording(onFailureAction: () -> Unit) {
        val name = "rec_${System.currentTimeMillis()}_${SystemClock.elapsedRealtime()}"
        if (voiceRecorderState.recorder.start(name)) {
            _mode.value = HamahangAudioBoxMode.Recording
            voiceRecorderState.resetTimer()
            voiceRecorderState.startTimer()
        } else {
            onFailureAction()
        }
    }

    fun stopRecording(onFailureAction: () -> Unit) {
        if (voiceRecorderState.recorder.stop()) {
            voiceRecorderState.pauseTimer()
            _mode.value = HamahangAudioBoxMode.Preview(voiceRecorderState.getRecordedFile())
        } else {
            onFailureAction()
        }
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
                sharedPref.safeGetInt(KEY_DEFAULT_VOICE_NAME_COUNTER, 1) + 1
            )
        }
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

        playerState.clear()
        voiceRecorderState.clear()
    }

    fun setUploadedFile(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uri == null) {
                _uiViewState.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
                return@launch
            }

            val absolutePath = if (uri.scheme == "file") {
                uri.toFile().absolutePath
            } else {
                createFileFromUri(uri)?.absolutePath
            }

            if (absolutePath.isNullOrBlank()) {
                _uiViewState.emit(UiError(uiException.getErrorMessageInvalidFile()))
                return@launch
            }

            val fileDuration = getFileDuration(absolutePath)
            if (fileDuration <= 0L) {
                _uiViewState.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
                return@launch
            }

            // Duplicate 2: check file durations
            if (fileDuration > MAX_FILE_DURATION_MS) {
                _uiViewState.emit(
                    UiError(
                        uiException.getErrorMessageMaxLengthExceeded((MAX_FILE_DURATION_MS / DateUtils.MINUTE_IN_MILLIS).toInt()),
                        isSnack = true
                    )
                )
                return@launch
            }

            _mode.value = HamahangAudioBoxMode.Preview(File(absolutePath))
        }
    }

    private suspend fun createFileFromUri(uri: Uri): File? {
        return fileCache.cacheUri(uri)
    }

    // Duplicate 3: file duration
    @WorkerThread
    private fun getFileDuration(filePath: String): Long {
        if (!this::retriever.isInitialized) return 0L

        return kotlin.runCatching {
            retriever.setDataSource(filePath)
            val time: String? =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong()
        }.getOrNull().orZero()
    }

    fun deleteFile() {
        playerState.stopPlaying()
        playerState.reset()
        kotlin.runCatching {
            (_mode.value as? HamahangAudioBoxMode.Preview)?.file?.delete()
        }
        _mode.value = HamahangAudioBoxMode.Idle
    }

    companion object {
        private const val KEY_DEFAULT_VOICE_NAME_COUNTER = "hamahangDefaultCounter"

        // note: 1m is shown in ui, if changed, change that as well
        private const val MAX_FILE_DURATION_MS = 1 * DateUtils.MINUTE_IN_MILLIS
    }
}