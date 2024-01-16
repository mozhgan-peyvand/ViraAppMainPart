package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.ui.UiStatus
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val IS_GRID_IMAZH_ARCHIVE_LIST_KEY = "isGridPrefKey_ImazhArchiveList"

@HiltViewModel
class ImazhArchiveListViewModel @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val repository: ImazhRepository,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    var isGrid = MutableStateFlow(true)
        private set

    val allArchiveFiles = repository.getAllProcessedFiles()
        .map { it.map { processedEntity -> processedEntity.toImazhProcessedFileView() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    val networkStatus = networkStatusTracker.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkStatus.Unavailable
    )

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    init {
        viewModelScope.launch {
            isGrid.value = sharedPref.getBoolean(IS_GRID_IMAZH_ARCHIVE_LIST_KEY, false)
        }
    }

    fun saveListType(value: Boolean) {
        viewModelScope.launch {
            isGrid.emit(value)
            sharedPref.edit {
                this.putBoolean(IS_GRID_IMAZH_ARCHIVE_LIST_KEY, value)
            }
        }
    }

    fun getImageBuilder(
        imageBuilder: ImageRequest.Builder,
        urlPath: String
    ): ImageRequest.Builder {
        return imageBuilder
            .setHeader("ApiKey", repository.sai())
            .data(repository.bi() + urlPath)
    }
}