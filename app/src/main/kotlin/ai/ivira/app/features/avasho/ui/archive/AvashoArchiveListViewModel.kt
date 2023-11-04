package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.features.avasho.data.AvashoRepository
import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.Downloading
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoProcessedFileView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatus.Unavailable
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.AppResult.Error
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// fixme these two are duplicated, in ArchiveListViewModel
private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val MAX_RETRY_COUNT = 3

@HiltViewModel
class AvashoArchiveListViewModel @Inject constructor(
    private val avashoRepository: AvashoRepository,
    private val uiException: UiException,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    // fix me implement it correctly
    private val _uiViewStat = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewStat

    private val _downloadQueue = MutableStateFlow(listOf<AvashoProcessedFileView>())
    val downloadQueue: StateFlow<List<AvashoProcessedFileView>> = _downloadQueue.asStateFlow()

    private val _downloadFileView = MutableStateFlow<AvashoProcessedFileView?>(null)

    private val _downloadStatus: MutableStateFlow<DownloadingFileStatus> = MutableStateFlow(
        IdleDownload
    )

    val networkStatus = networkStatusTracker.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Unavailable
    )

    private var job: Job? = null
    private var downloadJob: Job? = null

    private var failureCount = 0

    val allArchiveFiles = combine(
        networkStatusTracker.networkStatus,
        _downloadStatus,
        avashoRepository.getAllArchiveFiles(),
        _downloadFileView,
        downloadQueue
    ) { networkStatus: NetworkStatus,
        downloadState: DownloadingFileStatus,
        avashoArchiveFilesEntity: AvashoArchiveFilesEntity,
        downloadingFile: AvashoProcessedFileView?,
        downloadQueue: List<AvashoProcessedFileView> ->

        if (networkStatus is Unavailable) {
            job?.cancel()
            downloadJob?.cancel()
            _downloadStatus.emit(IdleDownload)
            _downloadFileView.emit(null)
        } else if (networkStatus is NetworkStatus.Available && !networkStatus.hasVpn) {
            if (
                downloadState != Downloading &&
                downloadQueue.isNotEmpty()
            ) {
                _uiViewStat.emit(UiLoading)
                _downloadStatus.value = Downloading

                downloadQueue.first().apply {
                    downloadFile(this)
                }
            }
        }

        // region archiveView
        val processedList = avashoArchiveFilesEntity.processed

        buildList {
            // TODO all tracking and uploading
            addAll(
                processedList.map {
                    it.toAvashoProcessedFileView(
                        downloadingPercent = downloadingFile?.downloadingPercent.orZero(),
                        downloadingId = downloadingFile?.id ?: -1,
                        fileSize = downloadingFile?.fileSize,
                        downloadedBytes = downloadingFile?.downloadedBytes
                    )
                }
            )
        }

        // endregion
    }.distinctUntilChanged()

    fun textToSpeechShort(
        speakerType: String,
        text: String,
        fileName: String
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = avashoRepository.convertToSpeechShort(
                text = text,
                speakerType = speakerType,
                fileName = fileName
            )

            handleResultState(result)
        }
    }

    fun addFileToDownloadQueue(id: AvashoProcessedFileView) {
        _downloadQueue.update {
            it.plus(id)
        }
    }

    private fun downloadFile(avashoProcessedFileView: AvashoProcessedFileView) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch(IO) {
            _downloadFileView.value = avashoProcessedFileView

            avashoRepository.downloadFile(
                id = avashoProcessedFileView.id,
                url = avashoProcessedFileView.fileUrl,
                fileName = avashoProcessedFileView.fileName
            ) { byteRead, totalSize ->
                val percent = byteRead / totalSize.toFloat()

                _downloadFileView.update { processedView ->
                    processedView?.copy(
                        id = avashoProcessedFileView.id,
                        downloadingPercent = percent,
                        downloadedBytes = byteRead,
                        fileSize = totalSize
                    )
                }
            }

            _downloadQueue.update { list ->
                list.filter {
                    it.id != avashoProcessedFileView.id
                }
            }
            _downloadStatus.emit(IdleDownload)
            _downloadFileView.emit(null)
        }
    }

    fun cancelDownload(id: Int) {
        _downloadQueue.update { it.filter { item -> item.id != id } }

        if (_downloadFileView.value?.id == id) {
            downloadJob?.cancel()
            downloadJob = null
            _downloadFileView.value = null
            _downloadStatus.value = IdleDownload
        }
    }

    fun isInDownloadQueue(id: Int): Boolean {
        return downloadQueue.value.any { it.id == id }
    }

    // fixme it's duplicate, in ArchiveListViewModel
    private suspend fun <T> handleResultState(
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        when (result) {
            is Success -> {
                failureCount = 0
                _uiViewStat.emit(UiSuccess)
                delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)

                // TODO emit Idle in Download state

                onSuccess?.let {
                    it(result)
                }
            }

            // onError we attempt to upload file 3 times, then emit failure and stop uploading
            is Error -> {
                failureCount++
                if (failureCount < MAX_RETRY_COUNT) {
                    // TODO emit Idle in Download state
                } else {
                    // TODO emit FailureUpload in Download state
                    _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                }
            }
        }
    }
}