package ai.ivira.app.features.imazh.ui.newImageDescriptor

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewImageDescriptorViewModel @Inject constructor() : ViewModel() {
    private val _prompt = mutableStateOf("")
    val prompt: State<String> = _prompt

    private val _selectedKeywords = mutableStateOf<Set<String>>(setOf())
    val selectedKeywords: State<Set<String>> = _selectedKeywords

    private val _selectedStyle: MutableState<String?> = mutableStateOf(null)
    val selectedStyle: State<String?> = _selectedStyle

    private val _negativePrompt = mutableStateOf("")
    val negativePrompt: State<String> = _negativePrompt

    fun changePrompt(newPrompt: String) {
        if (newPrompt.length <= PROMPT_CHARACTER_LIMIT) {
            _prompt.value = newPrompt
        }
    }

    fun resetPrompt() {
        _prompt.value = ""
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

    companion object {
        const val PROMPT_CHARACTER_LIMIT = 500
        const val NEGATIVE_PROMPT_CHARACTER_LIMIT = 300
    }
}