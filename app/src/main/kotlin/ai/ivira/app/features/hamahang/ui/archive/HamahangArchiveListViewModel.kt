package ai.ivira.app.features.hamahang.ui.archive

import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.Downloading
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.FailureDownload
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.IdleDownload
import ai.ivira.app.features.hamahang.data.HamahangRepository
import ai.ivira.app.features.hamahang.data.entity.HamahangArchiveFilesEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangCheckingFileEntity
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangArchiveView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangCheckingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangSpeakerView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangTrackingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangUploadingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.toHamahangCheckingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.toHamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.archive.model.toHamahangTrackingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.toHamahangUploadingFileView
import ai.ivira.app.utils.common.file.SaveToDownloadsHelper
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.combine
import ai.ivira.app.utils.ui.stateIn
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.State
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

// changeStateDelay duplicate 3
private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L

@HiltViewModel
class HamahangArchiveListViewModel @Inject constructor(
    private val repository: HamahangRepository,
    private val saveToDownloadsHelper: SaveToDownloadsHelper,
    private val application: Application,
    private val uiException: UiException,
    private val sharedPref: SharedPreferences,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private var retriever: MediaMetadataRetriever? = null
    private val currentDownloadingFile = MutableStateFlow<HamahangProcessedFileView?>(null)
    private val currentUploadingFile = MutableStateFlow<HamahangUploadingFileView?>(null)

    private val _uiViewState = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewState

    private var checkingQueue = listOf<HamahangCheckingFileView>()
    private var uploadingList = listOf<HamahangUploadingFileView>()
    private val downloadQueue = MutableStateFlow(listOf<HamahangProcessedFileView>())

    private val _downloadFailureList = MutableStateFlow(listOf<Int>())
    val downloadFailureList = _downloadFailureList.asStateFlow()

    private val _uploadStatus = MutableStateFlow<UploadingFileStatus>(UploadingFileStatus.Idle)

    private val _downloadStatus = MutableStateFlow<DownloadingFileStatus>(IdleDownload)
    val downloadStatus = _downloadStatus.asStateFlow()

    private var _isUploadingAllowed = MutableStateFlow(true)
    val isUploadingAllowed = _isUploadingAllowed.asStateFlow()

    private var indexOfItemThatShouldBeUploaded = 0

    private var job: Job? = null
    private var checkAudioJob: Job? = null
    private var downloadJob: Job? = null

    val networkStatus = networkStatusTracker.networkStatus.stateIn(initial = NetworkStatus.Unavailable)

    var processArchiveFileList = mutableStateOf<List<HamahangProcessedFileView>>(emptyList())
        private set

    private var _hasPermissionDeniedPermanently = mutableStateOf(false)
    val hasPermissionDeniedPermanently: State<Boolean> = _hasPermissionDeniedPermanently

    val isDownloadQueueEmpty = combine(downloadQueue, downloadFailureList) { queue, failure ->
        queue.isEmpty() && failure.isEmpty()
    }.stateIn(initial = true)

    private var _failureListId = MutableStateFlow(listOf<String>())
    val failureList = _failureListId.asStateFlow()

    var selectedHamahangItem = mutableStateOf<HamahangArchiveView?>(null)

    val allArchiveFiles = combine(
        networkStatusTracker.networkStatus,
        _uploadStatus,
        _downloadStatus,
        repository.getArchiveFiles(),
        currentDownloadingFile,
        currentUploadingFile,
        _downloadFailureList,
        downloadQueue
    ) { networkStatus: NetworkStatus,
        uploadState: UploadingFileStatus,
        downloadState: DownloadingFileStatus,
        hamahangArchiveFilesEntity: HamahangArchiveFilesEntity,
        downloadingFile: HamahangProcessedFileView?,
        uploadingFile: HamahangUploadingFileView?,
        failureList: List<Int>,
        downloadQueueList: List<HamahangProcessedFileView> ->

        val uploadingList = hamahangArchiveFilesEntity.uploading
        val checkingList = hamahangArchiveFilesEntity.checking
        val filteredCheckingList = hamahangArchiveFilesEntity.checking.filter { it.isProper }

        when (networkStatus) {
            is NetworkStatus.Unavailable -> resetEverything()

            is NetworkStatus.Available -> {
                val isRequestAllowed = uploadState != UploadingFileStatus.Uploading &&
                    uploadState != UploadingFileStatus.FailureUpload

                if (isRequestAllowed && filteredCheckingList.isNotEmpty()) {
                    _uiViewState.emit(UiLoading)
                    _uploadStatus.value = UploadingFileStatus.Uploading

                    with(filteredCheckingList.first()) {
                        checkAudioContent(
                            id = id,
                            speaker = speaker,
                            filePath = inputFilePath
                        )
                    }
                }

                if (isRequestAllowed && uploadingList.isNotEmpty()) {
                    _uiViewState.emit(UiLoading)
                    _uploadStatus.value = UploadingFileStatus.Uploading

                    indexOfItemThatShouldBeUploaded.run {
                        val id = uploadingList[this].id
                        val title = uploadingList[this].title
                        val file = File(uploadingList[this].inputFilePath)
                        val speaker = uploadingList[this].speaker

                        currentUploadingFile.update { uploadingList[this].toHamahangUploadingFileView() }

                        voiceConversion(
                            id = id,
                            speaker = speaker,
                            title = title,
                            file = file
                        )
                    }
                }

                if (downloadState == IdleDownload) {
                    if (downloadQueueList.isNotEmpty()) {
                        val item = downloadQueueList.firstOrNull { it.id !in failureList && !File(it.filePath).exists() }
                        if (item != null) {
                            _downloadStatus.update { Downloading }
                            downloadFile(item)
                            _downloadFailureList.update { it.filter { id -> id != item.id } }
                        }
                    }
                }
            }
        }

        // region archiveView
        val processedList = hamahangArchiveFilesEntity.processed
        val trackingList = hamahangArchiveFilesEntity.tracking

        _isUploadingAllowed.update {
            trackingList.isEmpty() && uploadingList.isEmpty() && _failureListId.value.isEmpty()
        }

        if (trackingList.isEmpty() && uploadingList.isEmpty()) {
            _uiViewState.emit(UiIdle)
        }

        buildList {

            addAll(
                checkingList.map { it.toHamahangCheckingFileView() }
                    .also { checkingList ->
                        this@HamahangArchiveListViewModel.checkingQueue = checkingList
                    }
            )

            addAll(
                uploadingList.map {
                    it.toHamahangUploadingFileView(
                        uploadingId = uploadingFile?.id ?: "",
                        uploadingPercent = uploadingFile?.uploadingPercent.orZero(),
                        uploadedBytes = uploadingFile?.uploadedBytes ?: -1
                    )
                }.also { uploadList ->
                    val uploadItem = uploadList.filter { uploadingItem ->
                        !_failureListId.value.contains(uploadingItem.id)
                    }

                    this@HamahangArchiveListViewModel.uploadingList = uploadItem
                }
            )
            addAll(trackingList.map { it.toHamahangTrackingFileView() })

            addAll(
                processedList.map {
                    it.toHamahangProcessedFileView(
                        downloadingPercent = downloadingFile?.downloadingPercent.orZero(),
                        downloadingId = downloadingFile?.id ?: -1,
                        downloadedBytes = downloadingFile?.downloadedBytes,
                        fileSize = downloadingFile?.fileSize,
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

    val isThereTrackingOrUploading = combine(allArchiveFiles, _failureListId) { files, failures ->
        val nonProcessedFiles = files.any {
            it is HamahangUploadingFileView ||
                it is HamahangTrackingFileView ||
                it is HamahangCheckingFileView
        }
        return@combine nonProcessedFiles || failures.isNotEmpty()
    }.stateIn(initial = false)

    init {

        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }

        viewModelScope.launch {
            _uploadStatus.collect {

                // on Idle and FailureUpload state, we reset everyThing
                if (it is UploadingFileStatus.Idle || it is UploadingFileStatus.FailureUpload) {
                    indexOfItemThatShouldBeUploaded = 0
                    currentUploadingFile.update { null }
                }
            }
        }
    }

    fun isInDownloadQueue(id: Int): Boolean {
        return downloadQueue.value.any { it.id == id }
    }

    fun cancelDownload(id: Int) {
        removeItemFromDownloadQueue(id)

        if (currentDownloadingFile.value?.id == id) {
            downloadJob?.cancel()
            downloadJob = null
            currentDownloadingFile.value = null
            _downloadStatus.value = IdleDownload
        }
    }

    fun startUploading(uploadingFile: HamahangUploadingFileView) {
        // to make sure that it does not return -1
        _failureListId.update { uploadingList ->
            uploadingList.filter { failureItemId -> failureItemId != uploadingFile.id }
        }
        with(uploadingList) {
            indexOfItemThatShouldBeUploaded = if (
                contains(uploadingFile)
            ) {
                indexOf(uploadingFile)
            } else {
                0
            }
        }

        _uploadStatus.value = UploadingFileStatus.IsNotUploading
    }

    fun markFileAsSeen(id: Int) = viewModelScope.launch(IO) {
        repository.markFileAsSeen(id = id, isSeen = true)
    }

    fun addFileToDownloadQueue(item: HamahangProcessedFileView) {
        downloadQueue.update { currentQueue ->
            if (currentQueue.contains(item)) {
                currentQueue
            } else {
                currentQueue.plus(item)
            }
        }

        if (_downloadStatus.value == FailureDownload) {
            _downloadStatus.value = IdleDownload
        }
    }

    fun tryDownloadAgain(item: HamahangProcessedFileView) {
        downloadQueue.update { currentQueue ->
            if (currentQueue.contains(item)) {
                currentQueue
            } else {
                currentQueue.plus(item)
            }
        }

        _downloadFailureList.update { list ->
            list.filter { it != item.id }
        }

        if (_downloadStatus.value == FailureDownload) {
            _downloadStatus.value = IdleDownload
        }
    }

    private suspend fun resetEverything() {
        job?.cancel()
        job = null
        checkAudioJob?.cancel()
        checkAudioJob = null
        downloadJob?.cancel()
        downloadJob = null
        _uploadStatus.emit(UploadingFileStatus.Idle)
        _downloadStatus.emit(IdleDownload)
        currentDownloadingFile.emit(null)
        currentUploadingFile.emit(null)
        with(downloadQueue.value) {
            if (this.isEmpty()) return@with

            _downloadFailureList.update { list ->
                list + this.map { it.id }
            }
        }
        downloadQueue.value = listOf()
    }

    private fun checkAudioContent(
        id: String,
        speaker: String,
        filePath: String
    ) {
        checkAudioJob?.cancel()
        checkAudioJob = viewModelScope.launch(IO) {
            val file = File(filePath)
            val result = repository.checkAudioValidity(
                id = id,
                file = file,
                speaker = speaker
            )

            handleResultState(id = id, result = result)
        }
    }

    private fun voiceConversion(
        id: String,
        speaker: String,
        title: String,
        file: File
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val result = repository.voiceConversion(
                id = id,
                title = title,
                file = file,
                listener = createProgressListener(),
                speaker = speaker
            )

            handleResultState(result = result, id = id)
        }
    }

    private fun downloadFile(processedFile: HamahangProcessedFileView) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch(IO) {
            currentDownloadingFile.value = processedFile

            val result = repository.downloadFile(
                id = processedFile.id,
                url = processedFile.fileUrl,
                fileName = processedFile.filePath
            ) { byteRead, totalSize ->
                val percent = byteRead / totalSize.toFloat()

                currentDownloadingFile.update { processedView ->
                    processedView?.copy(
                        id = processedFile.id,
                        downloadingPercent = percent,
                        downloadedBytes = byteRead
                    )
                }
            }

            removeItemFromDownloadQueue(processedFile.id)

            if (result is AppResult.Success) {
                _downloadStatus.emit(IdleDownload)

                _downloadFailureList.update { list ->
                    list.filter { it != processedFile.id }
                }
            } else {
                _downloadFailureList.update { failureList ->
                    if (failureList.contains(processedFile.id)) {
                        failureList
                    } else {
                        failureList.plus(processedFile.id)
                    }
                }

                if (downloadQueue.value.isEmpty()) {
                    _downloadStatus.update { FailureDownload }
                }
            }

            currentDownloadingFile.emit(null)
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

    // handleResultState Duplicate 4
    private suspend fun <T> handleResultState(
        id: String,
        result: AppResult<T>,
        onSuccess: ((AppResult.Success<T>) -> Unit)? = null
    ) {
        job = null
        checkAudioJob = null
        when (result) {
            is AppResult.Success -> {
                _uiViewState.emit(UiSuccess)
                delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)
                _uploadStatus.value = UploadingFileStatus.Idle

                onSuccess?.let {
                    it(result)
                }
            }

            is AppResult.Error -> {
                _failureListId.update { failureList ->
                    if (failureList.contains(id)) {
                        failureList
                    } else {
                        failureList + id
                    }
                }

                _uploadStatus.value = UploadingFileStatus.FailureUpload
                _uiViewState.emit(UiError(uiException.getErrorMessage(result.error)))
            }
        }
    }

    // createProgressListener duplicate 2
    private fun createProgressListener() = object : UploadProgressCallback {
        override fun onProgress(
            id: String,
            bytesUploaded: Long,
            totalBytes: Long,
            isDone: Boolean
        ) {
            currentUploadingFile.update {
                it?.copy(
                    uploadingPercent = (bytesUploaded.toDouble() / totalBytes).toFloat(),
                    uploadedBytes = bytesUploaded
                )
            }
        }
    }

    fun addFileToChecking(inputPath: String, speaker: HamahangSpeakerView, title: String) {
        viewModelScope.launch(IO) {
            repository.insertCheckingFile(
                HamahangCheckingFileEntity(
                    id = "${System.currentTimeMillis()}_$speaker",
                    title = title,
                    inputFilePath = inputPath,
                    speaker = speaker.serverName,
                    isProper = true,
                    createdAt = PersianDate().time
                )
            )
        }
    }

    fun deleteTrackingFile(token: String) =
        viewModelScope.launch(IO) {
            repository.deleteTrackingFile(token)
        }

    fun removeUploadingFile(item: HamahangUploadingFileView?) = viewModelScope.launch {
        item?.let {
            _failureListId.update { list ->
                list.filter { uploadingFailure -> uploadingFailure != item.id }
            }

            viewModelScope.launch {
                job?.cancel()
                // emit failure to start uploading from the first of list
                _uploadStatus.value = UploadingFileStatus.FailureUpload
                repository.deleteUploadingFile(it.id)
            }
        }
    }

    fun deleteCheckingFile(id: String, filePath: String) =
        viewModelScope.launch(IO) {
            repository.deleteCheckingFile(id = id, filePath = filePath)
        }

    fun deleteProcessedFile(id: Int, filePath: String) =
        viewModelScope.launch(IO) {
            repository.deleteProcessedFile(id = id, filePath = filePath)
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

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        val hasDenied = sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
        _hasPermissionDeniedPermanently.value = hasDenied
        return hasDenied
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) =
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }

    fun updateTitle(title: String, id: Int) =
        viewModelScope.launch(IO) {
            repository.updateTitle(title, id)
        }
}