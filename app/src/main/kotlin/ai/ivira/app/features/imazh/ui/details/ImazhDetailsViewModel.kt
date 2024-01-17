package ai.ivira.app.features.imazh.ui.details

import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImazhDetailsViewModel @Inject constructor(
    private val imazhRepository: ImazhRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val archiveFile = savedStateHandle.getStateFlow("id", -1)
        .flatMapLatest { id ->
            if (id == -1) {
                flowOf(null)
            } else {
                imazhRepository.getPhotoInfo(id).map { it?.toImazhProcessedFileView() }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun removeImage(id: Int, imagePath: String) {
        viewModelScope.launch(IO) {
            imazhRepository.deletePhotoInfo(id)
            runCatching {
                File(imagePath).delete()
            }
        }
    }

    fun getImageBuilder(
        imageBuilder: ImageRequest.Builder,
        urlPath: String
    ): ImageRequest.Builder {
        return imageBuilder
            .setHeader("ApiKey", imazhRepository.sai())
            .data(imazhRepository.bi() + urlPath)
    }
}