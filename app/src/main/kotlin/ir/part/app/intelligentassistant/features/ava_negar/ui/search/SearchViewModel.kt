package ir.part.app.intelligentassistant.features.ava_negar.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarRepository
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AvanegarRepository
) : ViewModel() {


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
                searchResult.filter {
                    it.title.contains(text)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _getSearchResult.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    init {
        getSearch(searchText.value)
    }

    private fun getSearch(title: String) = viewModelScope.launch {
        repository.getSearch(title).collect { processed ->
            _getSearchResult.value = processed.map {
                it.toAvanegarProcessedFileView()
            }
        }
    }


}