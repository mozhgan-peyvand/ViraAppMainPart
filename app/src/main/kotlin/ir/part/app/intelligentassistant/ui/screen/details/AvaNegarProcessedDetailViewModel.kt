package ir.part.app.intelligentassistant.ui.screen.details

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.data.AvanegarRepository
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarProcessedFileView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvaNegarProcessedDetailViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _processItemId =
        mutableIntStateOf(savedStateHandle.get<Int>("id") ?: 0)
    var processItemId = _processItemId


    private var _archiveFile =
        MutableStateFlow<AvanegarProcessedFileView?>(null)
    var archiveFile = _archiveFile.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getArchiveFile(processItemId.intValue).collect {
                _archiveFile.value = it?.toAvanegarProcessedFileView()
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
}