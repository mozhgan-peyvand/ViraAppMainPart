package ai.ivira.app.features.ava_negar.ui.details

import ai.ivira.app.features.ava_negar.data.AvanegarRepository
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.utils.common.orZero
import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.IntState
import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val UNDO_REDO_LIMIT = 50

@HiltViewModel
class ArchiveDetailViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) { // TODO: remove application

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    private val _fileNotExist = mutableStateOf(false)
    val fileNotExist: State<Boolean> = _fileNotExist

    private val _processItemId =
        mutableIntStateOf(savedStateHandle.get<Int>("id").orZero())
    val processItemId: IntState = _processItemId.asIntState()

    private val _archiveFile =
        MutableStateFlow<AvanegarProcessedFileView?>(null)
    val archiveFile = _archiveFile.asStateFlow()

    private var textList = mutableListOf("default")
    private val currentIndex = mutableIntStateOf(textList.size - 1)

    private var _textBody = mutableStateOf("")
    var textBody = _textBody

    var jobConverting: Job? = null
    var fileToShare: File? = null

    init {
        viewModelScope.launch {
            val id = processItemId.intValue
            repository.getArchiveFile(id).collect {
                _archiveFile.value = it?.toAvanegarProcessedFileView()

                it?.toAvanegarProcessedFileView()?.text?.let { text ->
                    _textBody.value = text
                    textList[0] = text
                }
                it?.filePath?.let { filepath ->
                    if (checkFileExisting(filepath)) {
                        playerState.tryInitWith(File(filepath))
                    } else {
                        _fileNotExist.value = true
                    }
                }
            }
        }
    }

    fun removeFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }

    fun setItemId(itemId: Int) {
        _processItemId.intValue = itemId
    }

    fun addTextToList(value: String) {
        if (textList.size >= UNDO_REDO_LIMIT) {
            textList.removeAt(0)
        }
        textList.add(value)
        _textBody.value = (value)
        currentIndex.intValue = textList.size - 1
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

    fun saveEditedText() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            repository.editText(textBody.value, _processItemId.intValue)
        }
    }

    private fun checkFileExisting(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        playerState.clear()
    }
}