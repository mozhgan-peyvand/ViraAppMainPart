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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val _getSearchResult =
        MutableStateFlow(listOf<AvanegarProcessedFileView>())

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val getSearchResult = searchText.debounce(1000L)
        .onEach {
            _isSearching.update { true }
        }
        .combine(_getSearchResult) { text, searchResult ->
            if (text.isBlank() || text.isEmpty()) {
                listOf()
            } else {
                delay(1000)
                _isSearching.value = false
                searchResult
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _getSearchResult.value
        )

    var jobConverting: Job? = null
    var fileToShare: File? = null

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    init {
        getSearch(searchText.value)
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }

    fun removeProcessedFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }

    private fun getSearch(title: String) = viewModelScope.launch {
        repository.getSearch(title).collect { processed ->
            _getSearchResult.value = processed.map {
                it.toAvanegarProcessedFileView()
            }
        }
    }
}