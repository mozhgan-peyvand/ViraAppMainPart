package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.Downloading
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.imazh.data.ImazhRepository
import ai.ivira.app.features.imazh.data.entity.ImazhArchiveFilesEntity
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.archive.model.toImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.archive.model.toImazhTrackingFileView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.shareMultipleImage
import ai.ivira.app.utils.ui.stateIn
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val _downloadFailureList = MutableStateFlow(listOf<Int>())
    val downloadFailureList = _downloadFailureList.asStateFlow()

    private val downloadStatus = MutableStateFlow<DownloadingFileStatus>(IdleDownload)

    private val currentDownloadingFile = MutableStateFlow<ImazhProcessedFileView?>(null)

    private var downloadJob: Job? = null

    val allArchiveFiles = combine(
        repository.getAllArchiveFiles(),
        networkStatusTracker.networkStatus,
        downloadStatus,
        downloadQueue,
        currentDownloadingFile
    ) { archiveFiles: ImazhArchiveFilesEntity,
        networkStatus: NetworkStatus,
        downloadState: DownloadingFileStatus,
        queue: List<ImazhProcessedFileView>,
        downloadingFile: ImazhProcessedFileView? ->

        if (networkStatus is NetworkStatus.Unavailable) {
            currentDownloadingFile.emit(null)
            downloadJob?.cancel()
            downloadJob = null
            downloadStatus.update { IdleDownload }
        } else {
            if (
                queue.isNotEmpty() &&
                downloadState == IdleDownload
            ) {
                downloadStatus.update { Downloading }
                downloadFile(queue.first())
                _downloadFailureList.update { it.filter { id -> id != queue.first().id } }
            }
        }

        val processedList = archiveFiles.processed
        val trackingList = archiveFiles.tracking

        buildList {
            addAll(trackingList.map { it.toImazhTrackingFileView() })
            addAll(
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
            )
        }
    }.stateIn(initial = emptyList())

    val filesInSelection = combine(downloadQueue, allArchiveFiles) { _, allArchiveFiles ->
        allArchiveFiles
            .filterIsInstance<ImazhProcessedFileView>()
            .filterNot { isInDownloadQueue(it.id) }
    }.stateIn(initial = emptyList())

    val networkStatus = networkStatusTracker.networkStatus.stateIn(NetworkStatus.Unavailable)

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private val _selectedItemIds = mutableStateOf(emptySet<Int>())
    val selectedItemIds: State<Set<Int>> = _selectedItemIds

    private var _isDeletingFiles = mutableStateOf(false)
    val isDeletingFiles: State<Boolean> = _isDeletingFiles

    private var _isSharingFiles = mutableStateOf(false)
    val isSharingFiles: State<Boolean> = _isSharingFiles

    private val _isSelectionMode = mutableStateOf(false)
    val isSelectionMode: State<Boolean> = _isSelectionMode

    init {
        viewModelScope.launch {
            isGrid.value = sharedPref.getBoolean(IS_GRID_IMAZH_ARCHIVE_LIST_KEY, false)
        }
    }

    fun setIsSharing(value: Boolean) {
        _isSharingFiles.value = value
    }

    private fun selectItems(vararg ids: Int) {
        _selectedItemIds.value = _selectedItemIds.value.plus(ids.toSet())
    }

    private fun deselectItems(vararg ids: Int) {
        _selectedItemIds.value = _selectedItemIds.value.minus(ids.toSet())
    }

    fun selectDeselectItems(vararg ids: Int) {
        ids.forEach {
            if (_selectedItemIds.value.contains(it)) {
                deselectItems(it)
            } else {
                selectItems(it)
            }
        }
    }

    fun selectAll() {
        _selectedItemIds.value = filesInSelection.value.map { it.id }.toSet()
    }

    fun deselectAll() {
        _selectedItemIds.value = emptySet()
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
                downloadStatus.update { IdleDownload }

                _downloadFailureList.update { list ->
                    list.filter { it == imazhProcessedFileView.id }
                }
            } else {
                _downloadFailureList.update { failureList ->
                    if (failureList.contains(imazhProcessedFileView.id)) {
                        failureList
                    } else {
                        failureList.plus(imazhProcessedFileView.id)
                    }
                }
            }

            currentDownloadingFile.emit(null)
            downloadJob = null
        }
    }

    fun startDownloading(item: ImazhProcessedFileView) {
        if (downloadStatus.value != Downloading) {
            downloadStatus.update { Downloading }

            downloadQueue.update { list ->
                list.plus(item.copy(downloadedBytes = null, downloadingPercent = 0f))
            }

            _downloadFailureList.update { list ->
                list.filter { it != item.id }
            }
            downloadFile(item)
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

    fun removeProcessedFile(id: Int, imagePath: String): Job {
        return viewModelScope.launch(IO) {
            repository.deleteProcessedFile(id)
            runCatching {
                File(imagePath).delete()
            }
        }
    }

    fun removeTrackingFile(token: String) {
        viewModelScope.launch(IO) {
            repository.deleteTrackingFile(token)
        }
    }

    fun shareSelectedItems(context: Context) {
        val files = filesInSelection.value
            .filter { file -> _selectedItemIds.value.any { it == file.id } }

        val uris = ArrayList(
            files.filter { File(it.filePath).exists() }
                .map {
                    FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".provider",
                        File(it.filePath)
                    )
                }
        )

        if (uris.isNotEmpty()) {
            setIsSharing(true)
            shareMultipleImage(context, uris)
        }
    }

    suspend fun deleteSelectedItems() {
        _isDeletingFiles.value = true
        filesInSelection.value
            .filter { file -> _selectedItemIds.value.any { it == file.id } }
            .map { removeProcessedFile(it.id, it.filePath) }
            .forEach { it.join() }
        _selectedItemIds.value = emptySet()
        _isDeletingFiles.value = false
        _isSelectionMode.value = false
    }

    fun clearSelectionMode() {
        _isSelectionMode.value = false
        _selectedItemIds.value = emptySet()
    }

    fun enableSelectionMode() {
        _isSelectionMode.value = true
    }
}