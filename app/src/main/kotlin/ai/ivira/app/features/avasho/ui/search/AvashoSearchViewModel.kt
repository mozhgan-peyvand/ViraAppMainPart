package ai.ivira.app.features.avasho.ui.search

import ai.ivira.app.features.avasho.data.AvashoRepository
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
import javax.inject.Inject

@HiltViewModel
class AvashoSearchViewModel @Inject constructor(
    private val repository: AvashoRepository
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _searchResult = MutableStateFlow<List<AvashoProcessedFileSearchView>>(emptyList())
    val searchResult = _searchResult.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private var queryJob: Job? = null

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
                val result = repository.searchAvashoArchiveItem(query).map {
                    it.toProcessFileSearchView()
                }
                _isSearching.update { false }
                _searchResult.value = result
            }
        }.launchIn(viewModelScope)
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}