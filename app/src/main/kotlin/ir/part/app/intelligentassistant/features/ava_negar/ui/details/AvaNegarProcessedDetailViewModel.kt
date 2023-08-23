package ir.part.app.intelligentassistant.features.ava_negar.ui.details

import androidx.compose.runtime.IntState
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarRepository
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
import ir.part.app.intelligentassistant.utils.common.orZero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UNDO_REDO_LIMIT = 50

@HiltViewModel
class AvaNegarProcessedDetailViewModel @Inject constructor(
        private val repository: AvanegarRepository,
        savedStateHandle: SavedStateHandle
) : ViewModel() {

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

    init {
        viewModelScope.launch {
            val id = processItemId.intValue
            repository.getArchiveFile(id).collect {
                _archiveFile.value = it?.toAvanegarProcessedFileView()

                it?.toAvanegarProcessedFileView()?.text?.let { text ->
                    _textBody.value = text
                    textList[0] = text
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
        if (textList.size >= UNDO_REDO_LIMIT)
            textList.removeAt(0)
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
}