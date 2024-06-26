package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.FailureUpload
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Idle
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.IsNotUploading
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Uploading
import ai.ivira.app.features.avasho.data.AvashoRepository
import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.features.avasho.ui.AvashoAnalytics
import ai.ivira.app.features.avasho.ui.archive.model.AvashoArchiveView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoUploadingFileView
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.Downloading
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.FailureDownload
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoTrackingFileView
import ai.ivira.app.features.avasho.ui.archive.model.toAvashoUploadingFileView
import ai.ivira.app.utils.common.file.SaveToDownloadsHelper
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
import ai.ivira.app.utils.ui.analytics.EventHandler
import ai.ivira.app.utils.ui.combine
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject

// changeStateDelay duplicate 2
private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val TEXT_LENGTH_LIMIT = 1000

@HiltViewModel
class AvashoArchiveListViewModel @Inject constructor(
    private val avashoRepository: AvashoRepository,
    private val uiException: UiException,
    private val application: Application,
    private val eventHandler: EventHandler,
    private val sharedPref: SharedPreferences,
    private val saveToDownloadsHelper: SaveToDownloadsHelper,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private var retriever: MediaMetadataRetriever? = null

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private var uploadingList = listOf<AvashoUploadingFileView>()

    private val _uploadingId = MutableStateFlow("")
    val uploadingId = _uploadingId.asStateFlow()

    private val downloadQueue = MutableStateFlow(listOf<AvashoProcessedFileView>())

    private val _downloadFailureList = MutableStateFlow(listOf<Int>())
    val downloadFailureList = _downloadFailureList.asStateFlow()

    private val downloadFileView = MutableStateFlow<AvashoProcessedFileView?>(null)

    private val _uploadStatus = MutableStateFlow<UploadingFileStatus>(Idle)

    private val _downloadStatus = MutableStateFlow<DownloadingFileStatus>(IdleDownload)
    val downloadStatus = _downloadStatus.asStateFlow()

    private var _isUploadingAllowed = MutableStateFlow(true)
    val isUploadingAllowed = _isUploadingAllowed.asStateFlow()

    private var indexOfItemThatShouldBeUploaded = 0

    private var playingAvashoItemId = mutableStateOf<Int>(-1)

    val networkStatus = networkStatusTracker.networkStatus.stateIn(initial = Unavailable)
    var selectedAvashoItemBottomSheet = mutableStateOf<AvashoArchiveView?>(null)
    var bottomSheetInitialValue = mutableStateOf(ViraBottomSheetValue.Hidden)

    private var job: Job? = null
    private var downloadJob: Job? = null

    var processArchiveFileList = mutableStateOf<List<AvashoProcessedFileView>>(emptyList())
        private set

    var isThereTrackingOrUploading = MutableStateFlow(false)
        private set

    private val filteredDownloadQueue = combine(
        _downloadFailureList,
        downloadQueue
    ) { downloadFailure: List<Int>, downloadQueue: List<AvashoProcessedFileView> ->

        buildList {
            for (i in downloadQueue) {
                if (File(i.filePath).exists() || downloadFailure.contains(i.id)) continue
                add(i)
            }
        }
    }

    val allArchiveFiles = combine(
        networkStatusTracker.networkStatus,
        _uploadStatus,
        _downloadStatus,
        avashoRepository.getAllArchiveFiles(),
        downloadFileView,
        filteredDownloadQueue
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
                if (
                    uploadState != Uploading &&
                    uploadState != FailureUpload &&
                    uploadingList.isNotEmpty()
                ) {
                    _uiViewState.emit(UiLoading)
                    _uploadStatus.value = Uploading

                    indexOfItemThatShouldBeUploaded.run {
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
                    downloadState == IdleDownload &&
                    downloadQueueList.isNotEmpty()
                ) {
                    _downloadStatus.value = Downloading
                    downloadFile(downloadQueueList.first())
                }
            }
        }

        // region archiveView
        val processedList = avashoArchiveFilesEntity.processed
        val trackingList = avashoArchiveFilesEntity.tracking

        _isUploadingAllowed.update {
            trackingList.isEmpty() && uploadingList.isEmpty()
        }

        if (trackingList.isEmpty() && uploadingList.isEmpty()) {
            _uiViewState.emit(UiIdle)
        }

        buildList {
            addAll(
                uploadingList.map {
                    it.toAvashoUploadingFileView()
                }.also {
                    isThereTrackingOrUploading.value =
                        trackingList.isNotEmpty() || uploadingList.isNotEmpty()

                    this@AvashoArchiveListViewModel.uploadingList = it
                }
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
                }.also { processedList ->
                    processArchiveFileList.value = processedList

                    for (i in processedList) {
                        if (File(i.filePath).exists() || downloadQueue.value.contains(i)) continue

                        addFileToDownloadQueue(i)
                    }
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
                    indexOfItemThatShouldBeUploaded = 0
                }
            }
        }
    }

    fun startUploading(avashoUploadingFileView: AvashoUploadingFileView) {
        // to make sure that it does not return -1
        with(uploadingList) {
            indexOfItemThatShouldBeUploaded = if (
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

        if (text.length <= TEXT_LENGTH_LIMIT) {
            eventHandler.specialEvent(AvashoAnalytics.createFileBelow1k)
        } else {
            eventHandler.specialEvent(AvashoAnalytics.createFileAbove1k)
        }

        viewModelScope.launch(IO) {
            avashoRepository.insertUploadingSpeech(
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

    fun addFileToDownloadQueue(item: AvashoProcessedFileView) {
        downloadQueue.update { currentQueue ->
            if (currentQueue.any { it.id == item.id }) {
                currentQueue
            } else {
                currentQueue.plus(item)
            }
        }

        if (_downloadStatus.value == FailureDownload) {
            _downloadStatus.value = IdleDownload
        }
    }

    fun retryDownload(id: Int) {
        _downloadFailureList.update { list ->
            list.filter { failureId ->
                failureId != id
            }
        }

        if (_downloadStatus.value == FailureDownload) {
            _downloadStatus.value = IdleDownload
        }
    }

    private fun downloadFile(avashoProcessedFileView: AvashoProcessedFileView) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch(IO) {
            downloadFileView.value = avashoProcessedFileView

            val result = avashoRepository.downloadFile(
                id = avashoProcessedFileView.id,
                url = avashoProcessedFileView.fileUrl,
                fileName = avashoProcessedFileView.title
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

            removeItemFromDownloadQueue(avashoProcessedFileView.id)

            if (result is Error) {
                _downloadFailureList.update { failureList ->
                    if (failureList.contains(avashoProcessedFileView.id)) {
                        failureList
                    } else {
                        failureList.plus(avashoProcessedFileView.id)
                    }
                }

                if (downloadQueue.value.isEmpty()) {
                    _downloadStatus.update { FailureDownload }
                }
            } else {
                _downloadStatus.emit(IdleDownload)

                _downloadFailureList.update { list ->
                    list.filter { it != avashoProcessedFileView.id }
                }
            }

            downloadFileView.emit(null)
            downloadJob = null
        }
    }

    private fun removeItemFromDownloadQueue(id: Int) {
        downloadQueue.update { list ->
            list.filter {
                it.id != id
            }
        }
    }

    fun cancelDownload(id: Int) {
        removeItemFromDownloadQueue(id)

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

    fun isItemPlaying(id: Int): Boolean {
        return playingAvashoItemId.value != -1 && playingAvashoItemId.value == id
    }

    private suspend fun resetEverything() {
        job?.cancel()
        job = null
        downloadJob?.cancel()
        downloadJob = null
        _uploadStatus.emit(Idle)
        _downloadStatus.emit(IdleDownload)
        downloadFileView.emit(null)
        with(downloadQueue.value) {
            if (this.isEmpty()) return@with

            _downloadFailureList.update { list ->
                list + this.map { it.id }
            }
        }
        downloadQueue.value = listOf()
    }

    // handleResultState Duplicate 2
    private suspend fun <T> handleResultState(
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        job = null
        when (result) {
            is Success -> {
                _uiViewState.emit(UiSuccess)
                delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)
                _uploadStatus.value = Idle

                onSuccess?.let {
                    it(result)
                }
            }

            is Error -> {
                _uploadStatus.value = FailureUpload
                _uiViewState.emit(UiError(uiException.getErrorMessage(result.error)))
            }
        }
    }

    fun updateTitle(title: String, id: Int) = viewModelScope.launch {
        avashoRepository.updateTitle(title = title, id = id)
    }

    fun removeTrackingFile(token: String) = viewModelScope.launch {
        avashoRepository.removeTrackingFile(token)
    }

    fun removeUploadingFile(id: String) = viewModelScope.launch {
        avashoRepository.removeUploadingFile(id)
    }

    fun removeProcessedFile(id: Int) = viewModelScope.launch {
        avashoRepository.deleteProcessFile(id)
    }

    fun saveToDownloadFolder(filePath: String, fileName: String): Boolean {
        return saveToDownloadsHelper.saveToDownloadFolder(
            filePath = filePath,
            fileName = fileName
        ).onFailure {
            viewModelScope.launch {
                _uiViewState.emit(
                    UiError(it.getErrorMessage(application), true)
                )
            }
        }.isSuccess
    }

    fun changePlayingItem(id: Int) {
        playingAvashoItemId.value = id
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }

    fun markFileAsSeen(id: Int) = viewModelScope.launch(IO) {
        avashoRepository.markFileAsSeen(id)
    }
}