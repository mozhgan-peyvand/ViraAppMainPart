package ir.part.app.intelligentassistant.ui.screen.archive

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.data.AvanegarRepository
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarTrackingFileView
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvaNegarArchiveViewModel @Inject constructor(
    private val repository: AvanegarRepository
) : ViewModel() {

    private val _allArchiveFiles: MutableState<List<ArchiveView>> = mutableStateOf(listOf())
    val allArchiveFiles: State<List<ArchiveView>> = _allArchiveFiles

    init {

        viewModelScope.launch {
            repository.getAllArchiveFiles().collect { avanegarArchiveFile ->
                val processedList = avanegarArchiveFile.processed
                val trackingList = avanegarArchiveFile.tracking
                _allArchiveFiles.value =
                    trackingList.map { it.toAvanegarTrackingFileView() } + processedList.map { it.toAvanegarProcessedFileView() }
            }
        }
    }
}