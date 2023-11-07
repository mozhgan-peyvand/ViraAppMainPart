package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.FailureUpload
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Idle
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.IsNotUploading
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Uploading
import ai.ivira.app.features.avasho.data.AvashoRepository
import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoUploadingFileView
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.Downloading
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoTrackingFileView
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoUploadingFileView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatus.Available
import ai.ivira.app.utils.data.NetworkStatus.Unavailable
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.AppResult.Error
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.combine
import android.media.MediaMetadataRetriever
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

// fixme these two are duplicated, in ArchiveListViewModel
private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val MAX_RETRY_COUNT = 3

private const val TEXT_LENGTH_LIMIT = 1000

@HiltViewModel
class AvashoArchiveListViewModel @Inject constructor(
    private val avashoRepository: AvashoRepository,
    private val uiException: UiException,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private var retriever: MediaMetadataRetriever? = null

    private val _uiViewStat = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewStat

    private var uploadingList: List<AvashoUploadingFileView> = listOf()

    private val _uploadingId: MutableStateFlow<String> = MutableStateFlow("")
    val uploadingId = _uploadingId.asStateFlow()

    private val downloadQueue = MutableStateFlow(listOf<AvashoProcessedFileView>())
    private val downloadFileView = MutableStateFlow<AvashoProcessedFileView?>(null)

    private val _uploadStatus: MutableStateFlow<UploadingFileStatus> = MutableStateFlow(Idle)
    private val _downloadStatus: MutableStateFlow<DownloadingFileStatus> = MutableStateFlow(
        IdleDownload
    )

    private var indexOfItemThatShouldBeDownloaded = 0

    var isThereAnyTrackingOrUploading = MutableStateFlow(false)
        private set

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
        _uploadStatus,
        _downloadStatus,
        avashoRepository.getAllArchiveFiles(),
        downloadFileView,
        downloadQueue
    ) { networkStatus: NetworkStatus,
        uploadState: UploadingFileStatus,
        downloadState: DownloadingFileStatus,
        avashoArchiveFilesEntity: AvashoArchiveFilesEntity,
        downloadingFile: AvashoProcessedFileView?,
        downloadQueueList: List<AvashoProcessedFileView> ->

        val uploadingList = avashoArchiveFilesEntity.uploading

        when (networkStatus) {
            is Unavailable -> resetEverything()

            is Available -> {
                if (networkStatus.hasVpn) resetEverything()

                if (
                    uploadState != Uploading &&
                    uploadState != FailureUpload &&
                    uploadingList.isNotEmpty()
                ) {
                    _uiViewStat.emit(UiLoading)
                    _uploadStatus.value = Uploading

                    indexOfItemThatShouldBeDownloaded.run {
                        _uploadingId.value = uploadingList[this].id
                        val id = uploadingList[this].id
                        val title = uploadingList[this].title
                        val text = uploadingList[this].text
                        val speaker = uploadingList[this].speaker

                        if (text.length <= TEXT_LENGTH_LIMIT) {
                            textToSpeechShort(
                                id = id,
                                fileName = title,
                                text = text,
                                speakerType = speaker
                            )
                        } else {
                            textToSpeechLong(
                                id = id,
                                fileName = title,
                                text = text,
                                speakerType = speaker
                            )
                        }
                    }
                }

                if (
                    downloadState != Downloading &&
                    downloadQueueList.isNotEmpty()
                ) {
                    _downloadStatus.value = Downloading
                    downloadQueueList.first().apply {
                        downloadFile(this)
                    }
                }
            }
        }

        // region archiveView
        val processedList = avashoArchiveFilesEntity.processed
        val trackingList = avashoArchiveFilesEntity.tracking

        isThereAnyTrackingOrUploading.value =
            trackingList.isNotEmpty() || uploadingList.isNotEmpty()

        if (trackingList.isEmpty() && uploadingList.isEmpty()) {
            _uiViewStat.emit(UiIdle)
        }

        buildList {
            addAll(
                uploadingList.map {
                    it.toAvashoUploadingFileView()
                }.also { this@AvashoArchiveListViewModel.uploadingList = it }
            )
            addAll(trackingList.map { it.toAvashoTrackingFileView() })

            addAll(
                processedList.map {
                    it.toAvashoProcessedFileView(
                        downloadingPercent = downloadingFile?.downloadingPercent.orZero(),
                        downloadingId = downloadingFile?.id ?: -1,
                        fileSize = downloadingFile?.fileSize,
                        downloadedBytes = downloadingFile?.downloadedBytes,
                        retriever = retriever
                    )
                }
            )
        }

        // endregion
    }.distinctUntilChanged()

    init {
        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }

        viewModelScope.launch {
            _uploadStatus.collect {

                // on Idle and FailureUpload state, we reset everyThing
                if (it is Idle || it is FailureUpload) {
                    indexOfItemThatShouldBeDownloaded = 0
                }
            }
        }
    }

    fun startUploading(avashoUploadingFileView: AvashoUploadingFileView) {
        // to make sure that it does not return -1
        with(uploadingList) {
            indexOfItemThatShouldBeDownloaded = if (
                contains(avashoUploadingFileView)
            ) {
                indexOf(avashoUploadingFileView)
            } else {
                0
            }
        }

        _uploadStatus.value = IsNotUploading
    }

    fun addToQueue(speakerType: String, text: String, fileName: String) {
        val partOfText = if (text.length < 10) text else text.substring(0, 9)
        val id = buildString {
            append(fileName)
            append("_")
            append(partOfText)
            append("_")
            append(speakerType)
        }

        viewModelScope.launch(IO) {
            avashoRepository.insertUploadingSpeechToDatabase(
                AvashoUploadingFileEntity(
                    id = id,
                    title = fileName,
                    text = text,
                    speaker = speakerType,
                    createdAt = PersianDate().time
                )
            )
        }
    }

    private fun textToSpeechShort(
        id: String,
        speakerType: String,
        text: String,
        fileName: String
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = avashoRepository.convertToSpeechShort(
                id = id,
                text = text,
                speakerType = speakerType,
                fileName = fileName
            )

            handleResultState(result)
        }
    }

    private fun textToSpeechLong(
        id: String,
        speakerType: String,
        text: String,
        fileName: String
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = avashoRepository.convertToSpeechLong(
                id = id,
                text = text,
                speakerType = speakerType,
                fileName = fileName
            )

            handleResultState(result)
        }
    }

    fun addFileToDownloadQueue(id: AvashoProcessedFileView) {
        downloadQueue.update {
            it.plus(id)
        }
    }

    private fun downloadFile(avashoProcessedFileView: AvashoProcessedFileView) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch(IO) {
            downloadFileView.value = avashoProcessedFileView

            avashoRepository.downloadFile(
                id = avashoProcessedFileView.id,
                url = avashoProcessedFileView.fileUrl,
                fileName = avashoProcessedFileView.fileName
            ) { byteRead, totalSize ->
                val percent = byteRead / totalSize.toFloat()

                downloadFileView.update { processedView ->
                    processedView?.copy(
                        id = avashoProcessedFileView.id,
                        downloadingPercent = percent,
                        downloadedBytes = byteRead,
                        fileSize = totalSize
                    )
                }
            }

            downloadQueue.update { list ->
                list.filter {
                    it.id != avashoProcessedFileView.id
                }
            }
            _downloadStatus.emit(IdleDownload)
            downloadFileView.emit(null)
        }
    }

    fun cancelDownload(id: Int) {
        downloadQueue.update { it.filter { item -> item.id != id } }

        if (downloadFileView.value?.id == id) {
            downloadJob?.cancel()
            downloadJob = null
            downloadFileView.value = null
            _downloadStatus.value = IdleDownload
        }
    }

    fun isInDownloadQueue(id: Int): Boolean {
        return downloadQueue.value.any { it.id == id }
    }

    private suspend fun resetEverything() {
        job?.cancel()
        job = null
        downloadJob?.cancel()
        downloadJob = null
        _uploadStatus.emit(Idle)
        _downloadStatus.emit(IdleDownload)
        downloadFileView.emit(null)
    }

    // fixme it's duplicate, in ArchiveListViewModel
    private suspend fun <T> handleResultState(
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        job = null
        when (result) {
            is Success -> {
                failureCount = 0
                _uiViewStat.emit(UiSuccess)
                delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)
                _uploadStatus.value = Idle

                onSuccess?.let {
                    it(result)
                }
            }

            // onError we attempt to upload file 3 times, then emit failure and stop uploading
            is Error -> {
                failureCount++
                if (failureCount < MAX_RETRY_COUNT) {
                    _uploadStatus.value = Idle
                } else {
                    _uploadStatus.value = FailureUpload
                    _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                }
            }
        }
    }
}