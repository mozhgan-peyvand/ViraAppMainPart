package ai.ivira.app.features.imazh.ui.newImageDescriptor

import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.ui.ImazhProcessImageStyle
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhHistoryView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.toImazhHistoryView
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewImageDescriptorViewModel @Inject constructor(
    private val imazhRepository: ImazhRepository,
    private val uiException: UiException
) : ViewModel() {
    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    private val _prompt = mutableStateOf("")
    val prompt: State<String> = _prompt

    private val _selectedKeywords = mutableStateOf<Set<String>>(setOf())
    val selectedKeywords: State<Set<String>> = _selectedKeywords

    private val _selectedStyle: MutableState<String?> = mutableStateOf(null)
    val selectedStyle: State<String?> = _selectedStyle

    private val _negativePrompt = mutableStateOf("")
    val negativePrompt: State<String> = _negativePrompt

    private val _historyList: MutableState<List<ImazhHistoryView>> = mutableStateOf(listOf())
    val historyList: State<List<ImazhHistoryView>> = _historyList

    private var promptIsEditedByUser by mutableStateOf(false)

    init {
        viewModelScope.launch(IO) {
            imazhRepository.getRecentHistory().collectLatest { list ->
                withContext(Main) {
                    _historyList.value = list.map { historyEntity ->
                        historyEntity.toImazhHistoryView()
                    }
                }
            }
        }
    }

    fun changePrompt(newPrompt: String) {
        if (newPrompt.length <= PROMPT_CHARACTER_LIMIT) {
            _prompt.value = newPrompt
        } else {
            _prompt.value = newPrompt.substring(startIndex = 0, endIndex = PROMPT_CHARACTER_LIMIT)
        }
        promptIsEditedByUser = true
    }

    fun resetPrompt() {
        changePrompt("")
        promptIsEditedByUser = false
    }

    fun generateRandomPrompt(confirmationCallback: () -> Unit) {
        if (_prompt.value.isBlank() || !promptIsEditedByUser) {
            changePrompt(imazhRepository.generateRandomPrompt())
            promptIsEditedByUser = false
        } else {
            confirmationCallback()
        }
    }

    fun selectStyle(newStyle: String) {
        if (newStyle.isNotBlank()) _selectedStyle.value = newStyle
    }

    fun resetSelectedStyle() {
        _selectedStyle.value = null
    }

    fun addKeywords(vararg newKeywords: String) {
        _selectedKeywords.value = _selectedKeywords.value.plus(newKeywords)
    }

    fun removeKeywords(vararg newKeywords: String) {
        _selectedKeywords.value = _selectedKeywords.value.minus(newKeywords.toSet())
    }

    fun resetKeywords() {
        _selectedKeywords.value = setOf()
    }

    fun setNegativePrompt(newNegativeWords: String) {
        if (newNegativeWords.length <= NEGATIVE_PROMPT_CHARACTER_LIMIT) {
            _negativePrompt.value = newNegativeWords
        }
    }

    fun resetNegativePrompt() {
        _negativePrompt.value = ""
    }

    fun sendRequest(
        prompt: String,
        negativePrompt: String,
        keywords: List<String>,
        style: ImazhProcessImageStyle = ImazhProcessImageStyle.Abstract
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiViewState.update { UiLoading }
            when (val result = imazhRepository.convertTextToImage(
                prompt,
                negativePrompt,
                keywords,
                style.value
            )) {
                is AppResult.Success -> {
                    _uiViewState.update {
                        UiSuccess
                    }
                }

                is AppResult.Error -> {
                    _uiViewState.update {
                        UiError(uiException.getErrorMessage(result.error))
                    }
                }
            }
        }
    }

    fun clearUiState() {
        _uiViewState.value = UiIdle
    }

    companion object {
        const val PROMPT_CHARACTER_LIMIT = 500
        const val NEGATIVE_PROMPT_CHARACTER_LIMIT = 300
    }
}