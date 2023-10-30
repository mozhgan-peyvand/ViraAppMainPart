package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.features.avasho.data.AvashoRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvashoArchiveListViewModel @Inject constructor(
    private val avashoRepository: AvashoRepository
) : ViewModel() {
    fun getSpeechFile(speakerType: String, text: String, fileName: String) =
        viewModelScope.launch {
            avashoRepository.convertToSpeechBelow1000(
                text = text,
                speakerType = speakerType,
                fileName = fileName
            )
        }
}