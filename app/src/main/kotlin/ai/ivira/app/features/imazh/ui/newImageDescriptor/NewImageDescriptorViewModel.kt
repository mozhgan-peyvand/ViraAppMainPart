package ai.ivira.app.features.imazh.ui.newImageDescriptor

import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.ui.ImazhAnalytics
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhHistoryView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.toImazhHistoryView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.toImazhKeywordView
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.analytics.EventHandler
import ai.ivira.app.utils.ui.stateIn
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val IMAZH_NEW_IMAGE_DESCRIPTOR_FIRST_RUN_KEY = "imazhNewImageDescriptorFirstRunKey"

@HiltViewModel
class NewImageDescriptorViewModel @Inject constructor(
    private val imazhRepository: ImazhRepository,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    private val eventHandler: EventHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    private val _prompt = mutableStateOf("")
    val prompt: State<String> = _prompt

    val imazhKeywords = imazhRepository.getKeywords().map { keywordsMap ->
        keywordsMap.mapValues { keywords -> keywords.value.map { it.toImazhKeywordView() }.toSet() }
    }.stateIn(initial = emptyMap())

    private val _selectedKeywords = mutableStateOf<List<ImazhKeywordView>>(emptyList())
    val selectedKeywords: State<List<ImazhKeywordView>> = _selectedKeywords

    private val _selectedStyle: MutableState<ImazhImageStyle> = mutableStateOf(ImazhImageStyle.None)
    val selectedStyle: State<ImazhImageStyle> = _selectedStyle

    val availableStyles = imazhRepository.getImageStyles().flowOn(IO)

    private val _negativePrompt = mutableStateOf("")
    val negativePrompt: State<String> = _negativePrompt

    private val _historyList: MutableState<List<ImazhHistoryView>> = mutableStateOf(listOf())
    val historyList: State<List<ImazhHistoryView>> = _historyList

    private var promptIsEditedByUser by mutableStateOf(false)

    private var job: Job? = null

    private var _promptIsValid = mutableStateOf(true)
    val promptIsValid: State<Boolean> = _promptIsValid

    private val _shouldShowFirstRun = mutableStateOf(
        sharedPref.getBoolean(IMAZH_NEW_IMAGE_DESCRIPTOR_FIRST_RUN_KEY, true)
    )
    val shouldShowFirstRun: State<Boolean> = _shouldShowFirstRun

    init {
        viewModelScope.launch {
            savedStateHandle.get<Int>("id")?.let { id ->
                if (id != -1) {
                    imazhRepository.getProcessedFileEntity(id)
                        ?.toImazhProcessedFileView()
                        ?.let { updateFieldsWithRegenerateTarget(it) }
                }
            }
        }
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

    private suspend fun updateFieldsWithRegenerateTarget(target: ImazhProcessedFileView) {
        val keywords = imazhKeywords.first().values.flatten()
            .filter { target.keywords.contains(it.keywordName) }
        changePrompt(target.prompt)
        _selectedKeywords.value = keywords
        _selectedStyle.value = target.style
        setNegativePrompt(target.negativePrompt)
    }

    fun doNotShowFirstRunAgain() {
        viewModelScope.launch {
            _shouldShowFirstRun.value = false
            sharedPref.edit()
                .putBoolean(IMAZH_NEW_IMAGE_DESCRIPTOR_FIRST_RUN_KEY, false)
                .apply()
        }
    }

    fun changePrompt(newPrompt: String) {
        if (newPrompt.length <= PROMPT_CHARACTER_LIMIT) {
            _prompt.value = newPrompt
        } else {
            _prompt.value = newPrompt.substring(startIndex = 0, endIndex = PROMPT_CHARACTER_LIMIT)
        }
        promptIsEditedByUser = true
        _promptIsValid.value = true
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

    fun selectStyle(newStyle: ImazhImageStyle) {
        eventHandler.specialEvent(ImazhAnalytics.selectStyle)
        _selectedStyle.value = newStyle
    }

    fun removeKeyword(newKeyword: String) {
        _selectedKeywords.value = _selectedKeywords.value.filter { keyword ->
            keyword.keywordName != newKeyword
        }
    }

    fun setNegativePrompt(newNegativeWords: String) {
        if (newNegativeWords.length <= NEGATIVE_PROMPT_CHARACTER_LIMIT) {
            _negativePrompt.value = newNegativeWords
        }
    }

    fun resetNegativePrompt() {
        _negativePrompt.value = ""
    }

    fun updateKeywordList(set: List<ImazhKeywordView>) {
        eventHandler.specialEvent(ImazhAnalytics.addKeywords)
        _selectedKeywords.value = set
    }

    fun generateImage() {
        viewModelScope.launch(IO) {
            _uiViewState.update { UiLoading }
            when (val result = imazhRepository.validatePromptAndConvertToImage(
                prompt.value,
                negativePrompt.value,
                selectedKeywords.value.map { it.toImazhKeywordEntity() },
                selectedStyle.value
            )) {
                is AppResult.Success -> {
                    _uiViewState.update {
                        UiSuccess
                    }.also {
                        _promptIsValid.value = result.data
                        if (result.data) {
                            eventHandler.specialEvent(ImazhAnalytics.createFile)
                        }
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

    fun handelBackButton(
        navigateUp: () -> Unit,
        backWhileEditing: () -> Unit,
        backWhileGenerating: () -> Unit
    ) {
        val isScreenEmpty = _prompt.value.isEmpty() && _negativePrompt.value.isEmpty() &&
            _selectedStyle.value == ImazhImageStyle.None && _selectedKeywords.value.isEmpty()

        if (_uiViewState.value == UiLoading) {
            backWhileGenerating()
        } else {
            if (isScreenEmpty) {
                navigateUp()
            } else {
                backWhileEditing()
            }
        }
    }

    fun cancelConvertTextToImageRequest() {
        job?.cancel()
    }

    companion object {
        const val PROMPT_CHARACTER_LIMIT = 500
        const val NEGATIVE_PROMPT_CHARACTER_LIMIT = 300
    }
}