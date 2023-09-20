package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

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
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarRepository
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.ArchiveView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus.FailureUpload
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus.Idle
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus.IsNotUploading
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus.Uploading
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.toAvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.toAvanegarTrackingFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.toAvanegarUploadingFileView
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEventPublisher
import ir.part.app.intelligentassistant.utils.common.file.FileCache
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import ir.part.app.intelligentassistant.utils.common.orZero
import ir.part.app.intelligentassistant.utils.data.NetworkStatus
import ir.part.app.intelligentassistant.utils.data.NetworkStatusTracker
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult.Error
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult.Success
import ir.part.app.intelligentassistant.utils.ui.UiError
import ir.part.app.intelligentassistant.utils.ui.UiException
import ir.part.app.intelligentassistant.utils.ui.UiIdle
import ir.part.app.intelligentassistant.utils.ui.UiLoading
import ir.part.app.intelligentassistant.utils.ui.UiStatus
import ir.part.app.intelligentassistant.utils.ui.UiSuccess
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val NUMBER_OF_REQUEST = 3
private const val IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY = "isGridPrefKey_AvanegarArchiveList"
private const val SIXTY_SECOND = 60000

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    private val fileCache: FileCache,
    private val aiEventPublisher: IntelligentAssistantEventPublisher,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    networkStatusTracker: NetworkStatusTracker,
) : ViewModel() {

    private val _uiViewStat = MutableSharedFlow<UiStatus>()
    val uiViewState: SharedFlow<UiStatus> = _uiViewStat

    private val _aiEvent: MutableState<IntelligentAssistantEvent?> = mutableStateOf(null)
    val aiEvent: State<IntelligentAssistantEvent?> = _aiEvent

    private val _allArchiveFiles: MutableState<List<ArchiveView>> = mutableStateOf(listOf())
    val allArchiveFiles: State<List<ArchiveView>> = _allArchiveFiles

    private val _isNetworkAvailable = mutableStateOf(false)
    val isNetworkAvailable: State<Boolean> = _isNetworkAvailable

    private var uploadingFileQueue = CopyOnWriteArrayList<AvanegarUploadingFileView>()
    private var indexOfItemThatShouldBeDownloaded = 0

    var isThereAnyTrackingOrUploading = MutableStateFlow(false)
        private set

    var isGrid = MutableStateFlow(true)
        private set

    //TODO set appropriate name
    var isSavingFile = false
        private set

    private val _isUploading: MutableStateFlow<UploadingFileStatus> = MutableStateFlow(Idle)
    val isUploading: StateFlow<UploadingFileStatus> = _isUploading.asStateFlow()

    private var files: MutableMap<Uri, File> = ConcurrentHashMap()

    private var numberOfRequest = NUMBER_OF_REQUEST

    private var job: Job? = null
    var jobConverting: Job? = null
    var fileToShare: File? = null

    //placed these variables in viewModel to save from configuration change,
    // can not make these, rememberSaveable because these are dataClass
    var archiveViewItem by mutableStateOf<ArchiveView?>(null)
    var processItem by mutableStateOf<AvanegarProcessedFileView?>(null)

    private lateinit var retriever: MediaMetadataRetriever

    init {
        kotlin.runCatching {
            retriever = MediaMetadataRetriever()
        }

        viewModelScope.launch {
            isGrid.value = sharedPref.getBoolean(IS_GRID_AVANEGAR_ARCHIVE_LIST_KEY, true)
        }

        viewModelScope.launch {
            networkStatusTracker.networkStatus.collect { isNetworkAvailable ->
                when (isNetworkAvailable) {
                    NetworkStatus.Available -> {
                        _isNetworkAvailable.value = true

                        if (_isUploading.value != Uploading && uploadingFileQueue.isNotEmpty()) {
                            startUploading()
                        }
                    }

                    NetworkStatus.Unavailable -> {

                        job?.cancel()
                        resetUploadProcess()
                        resetIndexOfItemThatShouldBeDownloaded()
                        _isNetworkAvailable.value = false
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.getAllArchiveFiles().collect { avanegarArchiveFile ->
                val processedList = avanegarArchiveFile.processed
                val trackingList = avanegarArchiveFile.tracking
                val uploadingList = avanegarArchiveFile.uploading
                isThereAnyTrackingOrUploading.value =
                    trackingList.isNotEmpty() || uploadingList.isNotEmpty()
                if (uploadingFileQueue.isEmpty()) uploadingFileQueue = CopyOnWriteArrayList(
                    uploadingList.map { it.toAvanegarUploadingFileView() }.reversed()
                )
                _allArchiveFiles.value =
                    uploadingList.map { it.toAvanegarUploadingFileView() } +
                            trackingList.map { it.toAvanegarTrackingFileView() } +
                            processedList.map { it.toAvanegarProcessedFileView() }

                if (_isUploading.value != Uploading)
                    startUploading()
            }
        }

        viewModelScope.launch {
            aiEventPublisher.events.collect {
                _aiEvent.value = it
            }
        }

        viewModelScope.launch {
            _isUploading.collect {

                if (it != Uploading && it != FailureUpload && uploadingFileQueue.isNotEmpty()) {
                    _uiViewStat.emit(UiLoading)
                    _isUploading.value = Uploading

                    indexOfItemThatShouldBeDownloaded.run {
                        val title = uploadingFileQueue[this].title
                        val filePath = uploadingFileQueue[this].filePath
                        val fileDuration = uploadingFileQueue[this].fileDuration

                        //0L means that file duration was is null
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

            if (!absolutePath.isNullOrBlank()) {
                val createdAt = PersianDate().time
                val id = title + absolutePath
                val fileDuration = getFileDuration(absolutePath)

                uploadingFileQueue.add(
                    AvanegarUploadingFileView(
                        id = id,
                        title = title,
                        filePath = absolutePath,
                        createdAt = createdAt,
                        fileDuration = fileDuration,
                        uploadedPercent = 0f, //it will not store in db
                        isUploadingFinished = false, //it will not store in db
                    )
                )

                insertUploadingFileToDatabase(
                    id = id,
                    title = title,
                    filePath = absolutePath,
                    createdAt = createdAt,
                    fileDuration = fileDuration
                )

                if (uploadingFileQueue.isNotEmpty() && _isUploading.value != Uploading) {
                    _uiViewStat.emit(UiLoading)

                    _isUploading.value = Uploading

                    if (fileDuration < SIXTY_SECOND && fileDuration != 0L)
                        audioToTextBelowSixtySecond(title, absolutePath, createProgressListener())
                    else
                        audioToTextAboveSixtySecond(title, absolutePath, createProgressListener())
                }

            } else _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile()))
        }
    }

    fun trackLargeFileResult(token: String) {
        viewModelScope.launch(IO) {
            val result = repository.trackLargeFileResult(token)

            withContext(Main) {
                when (result) {
                    is Success -> {
                        //TODO should not emit state
                        _uiViewStat.emit(UiSuccess)
                    }

                    is Error -> {
                        //TODO should not emit state
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

            //fixme set the correct value
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

    fun startUploading(avanegarUploadingFile: AvanegarUploadingFileView? = null) {
        //to make sure that it does not return -1
        indexOfItemThatShouldBeDownloaded = if (avanegarUploadingFile != null &&
            uploadingFileQueue.contains(avanegarUploadingFile)
        ) uploadingFileQueue.indexOf(avanegarUploadingFile)
        else 0

        _isUploading.value = IsNotUploading
    }

    private fun createProgressListener() = object : UploadProgressCallback {
        override fun onProgress(
            id: String,
            bytesUploaded: Long,
            totalBytes: Long,
            isDone: Boolean
        ) {
            _allArchiveFiles.value = _allArchiveFiles.value.map {
                when (it) {
                    is AvanegarUploadingFileView -> {
                        if (id != it.id) it
                        else {
                            it.copy(
                                uploadedPercent = (bytesUploaded.toDouble() / totalBytes).toFloat()
                            )
                        }
                    }

                    else -> it
                }
            }
        }
    }

    private fun resetUploadProcess() {
        _allArchiveFiles.value = _allArchiveFiles.value.map {
            if (it is AvanegarUploadingFileView) {
                it.copy(uploadedPercent = 0f)
            } else it
        }
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
                uploadingFileQueue =
                    CopyOnWriteArrayList(uploadingFileQueue.filter { it.id != result.data })
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
            if (this::retriever.isInitialized)
                retriever.release()
        }
    }
}