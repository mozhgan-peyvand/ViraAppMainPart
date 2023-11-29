package ai.ivira.app.features.ava_negar.ui.archive

import ai.ivira.app.features.ava_negar.data.AvanegarArchiveFilesEntity
import ai.ivira.app.features.ava_negar.data.AvanegarRepository
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
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
import ai.ivira.app.features.ava_negar.ui.record.VoiceRecordingViewModel.Companion.MAX_FILE_DURATION_MS
import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.event.ViraPublisher
import ai.ivira.app.utils.common.file.FileCache
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.ifFailure
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
import ai.ivira.app.utils.ui.analytics.EventHandler
import android.content.Context
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
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY = "isGridPrefKey_AvanegarArchiveList"
private const val SIXTY_SECOND = 60000

@HiltViewModel
class ArchiveListViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    private val fileCache: FileCache,
    private val aiEventPublisher: ViraPublisher,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    private val eventHandler: EventHandler, // TODO: should this be used here?
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private val _uiViewStat = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewStat

    private val _aiEvent: MutableState<ViraEvent?> = mutableStateOf(null)
    val aiEvent: State<ViraEvent?> = _aiEvent

    val networkStatus = networkStatusTracker.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkStatus.Unavailable
    )

    private val _isUploading: MutableStateFlow<UploadingFileStatus> = MutableStateFlow(Idle)
    val isUploading: StateFlow<UploadingFileStatus> = _isUploading.asStateFlow()

    private val _uploadingId: MutableStateFlow<String> = MutableStateFlow("")
    val uploadingId: StateFlow<String> = _uploadingId.asStateFlow()

    private var indexOfItemThatShouldBeDownloaded = 0

    private val uploadPercent = MutableStateFlow(0f)

    var isThereAnyTrackingOrUploadingOrFailure = MutableStateFlow(false)
        private set

    private var _isUploadingAllowed = MutableStateFlow(true)
    val isUploadingAllowed = _isUploadingAllowed.asStateFlow()

    var isGrid = MutableStateFlow(true)
        private set

    private var files: MutableMap<Uri, File> = ConcurrentHashMap()
    private var job: Job? = null
    var jobConverting: Job? = null
    var fileToShare: File? = null

    private var uploadList = MutableStateFlow(listOf<AvanegarUploadingFileView>())

    private var _failureList = MutableStateFlow(listOf<AvanegarUploadingFileView>())
    val failureList = _failureList.asStateFlow()

    // placed these variables in viewModel to save from configuration change,
    // can not make these, rememberSaveable because these are dataClass
    var archiveViewItem by mutableStateOf<ArchiveView?>(null)
    var processItem by mutableStateOf<AvanegarProcessedFileView?>(null)

    val allArchiveFiles = combine(
        networkStatusTracker.networkStatus,
        _isUploading,
        uploadPercent,
        uploadList,
        repository.getAllArchiveFiles()
    ) { networkStatus: NetworkStatus,
        uploadingFileStatus: UploadingFileStatus,
        uploadPercent: Float,
        uploadList,
        avanegarArchiveFilesEntity: AvanegarArchiveFilesEntity ->

        if (networkStatus is NetworkStatus.Unavailable) {
            job?.cancel()
            _isUploading.emit(Idle)
        } else if (
            uploadingFileStatus != Uploading &&
            uploadingFileStatus != FailureUpload &&
            uploadList.isNotEmpty()
        ) {
            _uiViewStat.emit(UiLoading)
            _isUploading.value = Uploading

            indexOfItemThatShouldBeDownloaded.run {
                _uploadingId.value = uploadList[this].id
                val item = uploadList[this]
                val fileDuration = uploadList[this].fileDuration

                // 0L means that file duration was is null
                if (fileDuration < SIXTY_SECOND) {
                    audioToTextBelowSixtySecond(
                        item,
                        createProgressListener()
                    )
                } else {
                    audioToTextAboveSixtySecond(
                        item,
                        createProgressListener()
                    )
                }
            }
        }

        // region archiveView
        val processedList = avanegarArchiveFilesEntity.processed
        val trackingList = avanegarArchiveFilesEntity.tracking
        // just ignore invalid files
        val uploadingList = avanegarArchiveFilesEntity.uploading.filter {
            it.fileDuration > 0L && File(it.filePath).exists()
        }

        _isUploadingAllowed.value = trackingList.isEmpty() && uploadList.isEmpty()

        if (trackingList.isEmpty() && uploadList.isEmpty()) {
            _uiViewStat.emit(UiIdle)
        }

        buildList {
            addAll(
                uploadingList.map { avanegarUploadingFile ->
                    avanegarUploadingFile.toAvanegarUploadingFileView(
                        uploadPercent,
                        uploadingId.value
                    )
                }.also { uploadingList ->
                    val uploadItem = uploadingList.filter { uploadingItem ->
                        !_failureList.value.contains(uploadingItem)
                    }

                    isThereAnyTrackingOrUploadingOrFailure.value =
                        trackingList.isNotEmpty() || uploadItem.isNotEmpty() || _failureList.value.isNotEmpty()

                    this@ArchiveListViewModel.uploadList.value = uploadItem
                }
            )
            addAll(trackingList.map { it.toAvanegarTrackingFileView() })
            addAll(processedList.map { it.toAvanegarProcessedFileView() })
        }

        // endregion
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

        viewModelScope.launch {
            _isUploading.collect {

                // on Idle and FailureUpload state, we reset everyThing
                if (it is Idle || it is FailureUpload) {
                    indexOfItemThatShouldBeDownloaded = 0
                    uploadPercent.update { 0f }
                }
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
                _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
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

            val fileDuration = getFileDuration(absolutePath)
            if (fileDuration <= 0L) {
                eventHandler.specialEvent(AvanegarAnalytics.unableToReadFileDuration)
                _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
                return@launch
            }

            // Duplicate 2: check file durations
            if (fileDuration >= MAX_FILE_DURATION_MS) {
                eventHandler.specialEvent(AvanegarAnalytics.fileDurationExceed)
                _uiViewStat.emit(
                    UiError(
                        uiException.getErrorMessageMaxLengthExceeded(),
                        isSnack = true
                    )
                )
                return@launch
            }

            if (fileDuration < SIXTY_SECOND) {
                eventHandler.specialEvent(AvanegarAnalytics.fileBelow60SecondsCreated)
            } else {
                eventHandler.specialEvent(AvanegarAnalytics.fileAbove60SecondsCreated)
            }

            val createdAt = PersianDate().time
            val id = title + absolutePath

            insertUploadingFileToDatabase(
                id = id,
                title = title,
                filePath = absolutePath,
                createdAt = createdAt,
                fileDuration = fileDuration
            )

            if (_isUploading.value == FailureUpload) {
                _isUploading.update {
                    Idle
                }
            }
        }
    }

    private suspend fun audioToTextBelowSixtySecond(
        item: AvanegarUploadingFileView,
        listener: UploadProgressCallback
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val file = File(item.filePath)
            val result = repository.audioToTextBelowSixtySecond(
                id = item.title + item.filePath,
                title = item.title,
                file = file,
                listener = listener
            )

            handleResultState(item, result)
        }
    }

    private suspend fun audioToTextAboveSixtySecond(
        item: AvanegarUploadingFileView,
        listener: UploadProgressCallback
    ) {
        job?.cancel()
        job = viewModelScope.launch(IO) {
            val file = File(item.filePath)
            val result = repository.audioToTextAboveSixtySecond(
                id = item.title + item.filePath,
                title = item.title,
                file = file,
                listener = listener
            )

            handleResultState(item, result)
        }
    }

    private suspend fun createFileFromUri(uri: Uri): File? {
        return files[uri] ?: fileCache.cacheUri(uri)?.also { file ->
            files[uri] = file
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

    @WorkerThread
    private fun getFileDuration(context: Context, uri: Uri?): Long {
        if (!this::retriever.isInitialized) return 0L

        return kotlin.runCatching {
            retriever.setDataSource(context, uri)
            val time: String? =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong()
        }.ifFailure { it?.printStackTrace() }.getOrNull().orZero()
    }

    suspend fun checkIfUriDurationIsOk(context: Context, uri: Uri?): Boolean {
        if (uri == null) {
            _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
            return false
        }

        val duration = getFileDuration(context, uri)
        if (duration <= 0L) {
            eventHandler.specialEvent(AvanegarAnalytics.unableToReadFileDuration)
            _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile(), isSnack = true))
            return false
        }

        // Duplicate 1: check file durations
        if (duration > MAX_FILE_DURATION_MS) {
            eventHandler.specialEvent(AvanegarAnalytics.fileDurationExceed)
            _uiViewStat.emit(
                UiError(
                    uiException.getErrorMessageMaxLengthExceeded(),
                    isSnack = true
                )
            )
            return false
        }

        return true
    }

    fun removeProcessedFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }

    fun removeTrackingFile(id: String?) = viewModelScope.launch {
        id?.let {
            repository.deleteUnprocessedFile(it)
        }
    }

    fun removeUploadingFile(item: AvanegarUploadingFileView?) = viewModelScope.launch {
        item?.let {
            _failureList.update { list ->
                list.filter { uploadingFailure -> uploadingFailure.id != item.id }
            }

            viewModelScope.launch {
                job?.cancel()
                // emit failure to start uploading from the first of list
                _isUploading.value = FailureUpload
                repository.deleteUploadingFile(it.id)
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

    fun startUploading(avanegarUploadingFile: AvanegarUploadingFileView) {
        // to make sure that it does not return -1

        _failureList.update { uploadingList ->
            uploadingList.filter { failureItem -> failureItem != avanegarUploadingFile }
        }

        with(uploadList.value) {
            indexOfItemThatShouldBeDownloaded = if (
                contains(avanegarUploadingFile)
            ) {
                indexOf(avanegarUploadingFile)
            } else {
                0
            }
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

    private suspend fun <T> handleResultState(
        item: AvanegarUploadingFileView,
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        when (result) {
            is Success -> {
                _uiViewStat.emit(UiSuccess)
                delay(CHANGE_STATE_TO_IDLE_DELAY_TIME)
                _isUploading.value = Idle

                uploadList.update { list ->
                    list.filter { uploadingItem -> uploadingItem != item }
                }

                onSuccess?.let {
                    it(result)
                }
            }

            is Error -> {
                _failureList.update { failureList ->
                    failureList + item
                }

                uploadList.update { list ->
                    list.filter { uploadingItem -> uploadingItem != item }
                }

                // to try upload all uploading items before emitting error
                if (uploadList.value.isEmpty()) {
                    _isUploading.value = FailureUpload
                    _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                } else {
                    _isUploading.value = Idle
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