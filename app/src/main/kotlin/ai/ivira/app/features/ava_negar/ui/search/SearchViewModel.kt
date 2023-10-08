package ai.ivira.app.features.ava_negar.ui.search

import ai.ivira.app.features.ava_negar.data.AvanegarRepository
import ai.ivira.app.features.ava_negar.ui.archive.model.ArchiveView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AvanegarRepository
) : ViewModel() {
    // placed these variables in viewModel to save from configuration change,
    // can not make these, rememberSaveable because these are dataClass
    var archiveViewItem by mutableStateOf<ArchiveView?>(null)
    var processItem by mutableStateOf<AvanegarProcessedFileView?>(null)

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchResult = MutableStateFlow<List<AvanegarProcessedFileView>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var queryJob: Job? = null
    var jobConverting: Job? = null
    var fileToShare: File? = null

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    init {
        searchText.onEach { query ->
            queryJob?.cancel()
            _searchResult.value = emptyList()
            if (query.isBlank()) {
                _isSearching.update { false }
                return@onEach
            }

            queryJob = viewModelScope.launch(Dispatchers.IO) {
                _isSearching.update { true }
                delay(1000)
                val result = repository.getSearch(query).map {
                    it.toAvanegarProcessedFileView()
                }
                _isSearching.update { false }
                _searchResult.value = result
            }
        }.launchIn(viewModelScope)
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }

    fun removeProcessedFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }
}