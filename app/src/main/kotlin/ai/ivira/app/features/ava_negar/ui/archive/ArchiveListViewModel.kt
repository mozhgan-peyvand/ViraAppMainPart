package ai.ivira.app.features.ava_negar.ui.archive

import ai.ivira.app.features.ava_negar.data.AvanegarArchiveFilesEntity
import ai.ivira.app.features.ava_negar.data.AvanegarRepository
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.ava_negar.ui.archive.model.ArchiveView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarUploadingFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.FailureUpload
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Idle
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.IsNotUploading
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus.Uploading
import ai.ivira.app.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.toAvanegarTrackingFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.toAvanegarUploadingFileView
import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.event.ViraPublisher
import ai.ivira.app.utils.common.file.FileCache
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
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
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val NUMBER_OF_REQUEST = 3
private const val IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY = "isGridPrefKey_AvanegarArchiveList"
private const val SIXTY_SECOND = 60000

@HiltViewModel
class ArchiveListViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    private val fileCache: FileCache,
    private val aiEventPublisher: ViraPublisher,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private val _uiViewStat = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewStat

    private val _aiEvent: MutableState<ViraEvent?> = mutableStateOf(null)
    val aiEvent: State<ViraEvent?> = _aiEvent

    @OptIn(ExperimentalCoroutinesApi::class)
    val isNetworkAvailable = networkStatusTracker.networkStatus.mapLatest {
        it is NetworkStatus.Available
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _isUploading: MutableStateFlow<UploadingFileStatus> = MutableStateFlow(Idle)
    val isUploading: StateFlow<UploadingFileStatus> = _isUploading.asStateFlow()

    private var indexOfItemThatShouldBeDownloaded = 0

    private val uploadPercent = MutableStateFlow(0f)

    var isThereAnyTrackingOrUploading = MutableStateFlow(false)
        private set

    var isGrid = MutableStateFlow(true)
        private set

    // TODO set appropriate name
    var isSavingFile = false
        private set

    private var files: MutableMap<Uri, File> = ConcurrentHashMap()

    private var numberOfRequest = NUMBER_OF_REQUEST

    private var job: Job? = null
    var jobConverting: Job? = null
    var fileToShare: File? = null

    // placed these variables in viewModel to save from configuration change,
    // can not make these, rememberSaveable because these are dataClass
    var archiveViewItem by mutableStateOf<ArchiveView?>(null)
    var processItem by mutableStateOf<AvanegarProcessedFileView?>(null)

    val allArchiveFiles = combine(
        networkStatusTracker.networkStatus,
        _isUploading,
        uploadPercent,
        repository.getAllArchiveFiles()
    ) { networkStatus: NetworkStatus, uploadingFileStatus: UploadingFileStatus, uploadPercent: Float, avanegarArchiveFilesEntity: AvanegarArchiveFilesEntity ->

        if (networkStatus is NetworkStatus.Unavailable) {
            job?.cancel()
            resetUploadProcess()
            resetIndexOfItemThatShouldBeDownloaded()
        } else {
            val uploadingList = avanegarArchiveFilesEntity.uploading
            // region uploadingFileStatus
            if (uploadingFileStatus != Uploading && uploadingFileStatus != IsNotUploading && uploadingFileStatus != FailureUpload && uploadingList.isNotEmpty()) {

                startUploading()
                _uiViewStat.emit(UiLoading)
                _isUploading.value = Uploading

                indexOfItemThatShouldBeDownloaded.run {
                    val title = uploadingList[this].title
                    val filePath = uploadingList[this].filePath
                    val fileDuration = uploadingList[this].fileDuration

                    // 0L means that file duration was is null
                    if (fileDuration < SIXTY_SECOND && fileDuration != 0L) {
                        audioToTextBelowSixtySecond(
                            title,
                            filePath,
                            createProgressListener()
                        )
                    } else {
                        audioToTextAboveSixtySecond(
                            title,
                            filePath,
                            createProgressListener()
                        )
                    }
                }
            }
            // endregion
        }

        // region archiveView
        val processedList = avanegarArchiveFilesEntity.processed
        val trackingList = avanegarArchiveFilesEntity.tracking
        val uploadingList = avanegarArchiveFilesEntity.uploading

        isThereAnyTrackingOrUploading.value =
            trackingList.isNotEmpty() || uploadingList.isNotEmpty()

        // endregion
        val title = if (uploadingList.isNotEmpty()) uploadingList[0].title else ""
        uploadingList.map { it.toAvanegarUploadingFileView(uploadPercent, title) } +
            trackingList.map { it.toAvanegarTrackingFileView() } +
            processedList.map { it.toAvanegarProcessedFileView() }
    }.distinctUntilChanged()

    private lateinit var retriever: MediaMetadataRetriever

    init {
        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }

        viewModelScope.launch {
            isGrid.value = sharedPref.getBoolean(IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY, true)
        }

        viewModelScope.launch {
            aiEventPublisher.events.collect {
                _aiEvent.value = it
            }
        }
    }

    fun saveListType(value: Boolean) {
        viewModelScope.launch {
            isGrid.emit(value)
            sharedPref.edit {
                this.putBoolean(IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY, value)
            }
        }
    }

    fun addFileToUploadingQueue(title: String, uri: Uri?) {
        viewModelScope.launch(IO) {
            if (uri == null) {
                _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile()))
                return@launch
            }

            val absolutePath = if (uri.scheme == "file") {
                uri.toFile().absolutePath
            } else {
                createFileFromUri(uri)?.absolutePath
            }

            if (absolutePath.isNullOrBlank()) {
                _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile()))
                return@launch
            }

            val createdAt = PersianDate().time
            val id = title + absolutePath
            val fileDuration = getFileDuration(absolutePath)

            insertUploadingFileToDatabase(
                id = id,
                title = title,
                filePath = absolutePath,
                createdAt = createdAt,
                fileDuration = fileDuration
            )
        }
    }

    fun trackLargeFileResult(token: String) {
        viewModelScope.launch(IO) {
            val result = repository.trackLargeFileResult(token)

            withContext(Main) {
                when (result) {
                    is Success -> {
                        // TODO should not emit state
                        _uiViewStat.emit(UiSuccess)
                    }

                    is Error -> {
                        // TODO should not emit state
                        _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                    }
                }
            }
        }
    }

    private suspend fun audioToTextBelowSixtySecond(
        title: String,
        filePath: String,
        listener: UploadProgressCallback
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val file = File(filePath)
            val result = repository.audioToTextBelowSixtySecond(
                id = title + filePath,
                title = title,
                file = file,
                listener = listener
            )

            handleResultState(result)
        }
    }

    private suspend fun audioToTextAboveSixtySecond(
        title: String,
        filePath: String,
        listener: UploadProgressCallback
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val file = File(filePath)
            val result = repository.audioToTextAboveSixtySecond(
                id = title + filePath,
                title = title,
                file = file,
                listener = listener
            )

            handleResultState(result)
        }
    }

    private fun changeUploadFileToIdle() {
        viewModelScope.launch {
            // fixme set the correct value
            delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)
        }
    }

    private suspend fun createFileFromUri(uri: Uri): File? {
        isSavingFile = true

        return files[uri] ?: fileCache.cacheUri(uri)?.also { file ->
            files[uri] = file
            isSavingFile = false
        }
    }

    private suspend fun insertUploadingFileToDatabase(
        id: String,
        title: String,
        filePath: String,
        createdAt: Long,
        fileDuration: Long
    ) {
        repository.insertUploadingFile(
            AvanegarUploadingFileEntity(
                id = id,
                title = title,
                filePath = filePath,
                createdAt = createdAt,
                fileDuration = fileDuration
            )
        )
    }

    @WorkerThread
    private fun getFileDuration(filePath: String): Long {
        if (!this::retriever.isInitialized) return 0L

        return kotlin.runCatching {
            retriever.setDataSource(filePath)
            val time: String? =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong()
        }.getOrNull().orZero()
    }

    fun cancelDownload() {
        job?.cancel()
        _uiViewStat.tryEmit(UiIdle)
    }

    fun updateIsSaving(value: Boolean) {
        isSavingFile = value
    }

    fun removeProcessedFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }

    fun removeTrackingFile(id: String?) = viewModelScope.launch {
        id?.let {
            repository.deleteUnprocessedFile(it)
        }
    }

    fun removeUploadingFile(id: String?) = viewModelScope.launch {
        id?.let {
            viewModelScope.launch {
                repository.deleteUploadingFile(it)
                resetUploadProcess()
                resetIndexOfItemThatShouldBeDownloaded()
                _isUploading.value = FailureUpload
            }
        }
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
    }

    fun startUploading(
        avanegarUploadingFile: AvanegarUploadingFileView? = null,
        uploadingList: List<AvanegarUploadingFileView>? = null
    ) {
        // to make sure that it does not return -1
        indexOfItemThatShouldBeDownloaded = if (avanegarUploadingFile != null &&
            uploadingList != null &&
            uploadingList.contains(
                avanegarUploadingFile
            )
        ) {
            uploadingList.indexOf(avanegarUploadingFile)
        } else {
            0
        }

        _isUploading.value = IsNotUploading
    }

    private fun createProgressListener() = object : UploadProgressCallback {
        override fun onProgress(
            id: String,
            bytesUploaded: Long,
            totalBytes: Long,
            isDone: Boolean
        ) {
            uploadPercent.update {
                (bytesUploaded.toDouble() / totalBytes).toFloat()
            }
        }
    }

    private fun resetUploadProcess() {
        uploadPercent.update { 0f }
    }

    private fun resetIndexOfItemThatShouldBeDownloaded() {
        indexOfItemThatShouldBeDownloaded = 0
    }

    private suspend fun <T> handleResultState(
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        when (result) {
            is Success -> {
                resetIndexOfItemThatShouldBeDownloaded()
                numberOfRequest = NUMBER_OF_REQUEST
                _uiViewStat.emit(UiSuccess)
                changeUploadFileToIdle()
                _isUploading.value = Idle

                onSuccess?.let {
                    it(result)
                }
            }

            is Error -> {
                if (numberOfRequest > 0) {
                    numberOfRequest--
                    _isUploading.value = Idle
                } else {
                    resetUploadProcess()
                    resetIndexOfItemThatShouldBeDownloaded()
                    _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                    _isUploading.value = FailureUpload
                }
            }
        }
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }

    override fun onCleared() {
        kotlin.runCatching {
            if (this::retriever.isInitialized) {
                retriever.release()
            }
        }
    }
}