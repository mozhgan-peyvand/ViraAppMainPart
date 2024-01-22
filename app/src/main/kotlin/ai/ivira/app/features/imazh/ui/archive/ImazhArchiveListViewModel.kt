package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiStatus
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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

    private val downloadQueue = MutableStateFlow(listOf<ImazhProcessedFileView>())
    private val downloadStatus = MutableStateFlow<DownloadingFileStatus>(IdleDownload)
    private val currentDownloadingFile = MutableStateFlow<ImazhProcessedFileView?>(null)

    private var downloadJob: Job? = null

    val allArchiveFiles = combine(
        repository.getAllProcessedFiles(),
        networkStatusTracker.networkStatus,
        downloadStatus,
        downloadQueue,
        currentDownloadingFile
    ) { processedList: List<ImazhProcessedEntity>,
        networkStatus: NetworkStatus,
        downloadState: DownloadingFileStatus,
        queue: List<ImazhProcessedFileView>,
        downloadingFile: ImazhProcessedFileView? ->

        if (
            queue.isNotEmpty() &&
            networkStatus is NetworkStatus.Available &&
            downloadState == IdleDownload
        ) {
            downloadStatus.update {
                DownloadingFileStatus.Downloading
            }
            downloadFile(queue.first())
        }

        processedList.map { processedEntity ->
            processedEntity.toImazhProcessedFileView(
                downloadingPercent = downloadingFile?.downloadingPercent.orZero(),
                downloadingId = downloadingFile?.id ?: -1,
                fileSize = downloadingFile?.fileSize,
                downloadedBytes = downloadingFile?.downloadedBytes
            )
        }.also { list ->
            val idList = queue.map { it.id }
            for (i in list) {
                if (File(i.filePath).exists() || idList.contains(i.id)) continue

                downloadQueue.update { queue ->
                    queue.plus(i)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
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

    private fun downloadFile(imazhProcessedFileView: ImazhProcessedFileView) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch(IO) {
            currentDownloadingFile.value = imazhProcessedFileView
            val result = repository.downloadFile(
                id = imazhProcessedFileView.id,
                url = imazhProcessedFileView.imagePath,
                fileName = imazhProcessedFileView.filePath
            ) { byteRead, totalSize ->
                val percent = byteRead / totalSize.toFloat()

                currentDownloadingFile.update { processedView ->
                    processedView?.copy(
                        id = imazhProcessedFileView.id,
                        downloadingPercent = percent,
                        downloadedBytes = byteRead,
                        fileSize = totalSize
                    )
                }
            }

            removeItemFromDownloadQueue(imazhProcessedFileView.id)

            if (result is AppResult.Success) {
                downloadStatus.emit(IdleDownload)
            }

            currentDownloadingFile.emit(null)
            downloadJob = null
        }
    }

    fun isInDownloadQueue(id: Int): Boolean {
        return downloadQueue.value.any { it.id == id }
    }

    private fun removeItemFromDownloadQueue(id: Int) {
        downloadQueue.update { list ->
            list.filter {
                it.id != id
            }
        }
    }

    fun removeImage(id: Int, imagePath: String) {
        viewModelScope.launch(IO) {
            repository.deletePhotoInfo(id)
            runCatching {
                File(imagePath).delete()
            }
        }
    }
}