package ai.ivira.app.features.avasho.ui.file_creation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AvashoFileCreationViewModel @Inject constructor() : ViewModel() {
    private var _textBody = mutableStateOf("")
    var textBody = _textBody

    fun addTextToList(value: String) {
        _textBody.value = (value)
    }
}