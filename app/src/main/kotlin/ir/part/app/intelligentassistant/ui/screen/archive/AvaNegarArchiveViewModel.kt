package ir.part.app.intelligentassistant.ui.screen.archive

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.data.AvanegarRepository
import ir.part.app.intelligentassistant.data.entity.AvanegarUploadingFileEntity
import ir.part.app.intelligentassistant.ui.screen.archive.UploadingFileStatus.FailureUpload
import ir.part.app.intelligentassistant.ui.screen.archive.UploadingFileStatus.Idle
import ir.part.app.intelligentassistant.ui.screen.archive.UploadingFileStatus.IsNotUploading
import ir.part.app.intelligentassistant.ui.screen.archive.UploadingFileStatus.Uploading
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarUploadingFileView
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEventPublisher
import ir.part.app.intelligentassistant.utils.common.file.FileCache
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
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

private const val DENIED_PERMISSION_KEY = "deniedPermissionKey"
private const val CHANGE_STATE_TO_IDLE_DELAY_TIME = 2000L
private const val NUMBER_OF_REQUEST = 3

@HiltViewModel
class AvaNegarArchiveViewModel @Inject constructor(
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

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private var uploadingFileQueue = CopyOnWriteArrayList<AvanegarUploadingFileView>()

    //TODO set appropriate name
    var isSavingFile = false
        private set

    private val _isUploading: MutableStateFlow<UploadingFileStatus> = MutableStateFlow(Idle)
    val isUploading: StateFlow<UploadingFileStatus> = _isUploading.asStateFlow()

    private var files: MutableMap<Uri, File> = ConcurrentHashMap()

    private var numberOfRequest = NUMBER_OF_REQUEST

    private var job: Job? = null

    init {
        viewModelScope.launch {
            networkStatusTracker.networkStatus.collect { isNetworkAvailable ->
                when (isNetworkAvailable) {
                    NetworkStatus.Available -> _isNetworkAvailable.emit(true)
                    NetworkStatus.Unavailable -> {

                        job?.cancel()
                        _allArchiveFiles.value = _allArchiveFiles.value.map {
                            if (it is AvanegarUploadingFileView) {
                                it.copy(uploadedPercent = 0f)
                            } else it
                        }
                        _isNetworkAvailable.emit(false)
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.getAllArchiveFiles().collect { avanegarArchiveFile ->
                val processedList = avanegarArchiveFile.processed
                val trackingList = avanegarArchiveFile.tracking
                val uploadingList = avanegarArchiveFile.uploading
                if (uploadingFileQueue.isEmpty())
                    uploadingFileQueue = CopyOnWriteArrayList(
                        uploadingList.map { it.toAvanegarUploadingFileView() }.reversed()
                    )
                _allArchiveFiles.value =
                    uploadingList.map { it.toAvanegarUploadingFileView() } +
                            trackingList.map { it.toAvanegarTrackingFileView() } +
                            processedList.map { it.toAvanegarProcessedFileView() }
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

                    //TODO should calculate audio file duration
                    audioToTextAboveSixtySecond(
                        uploadingFileQueue.first().title,
                        uploadingFileQueue.first().filePath,
                        createProgressListener()
                    )
                }
            }
        }
    }

    fun addFileToUploadingQueue(title: String, uri: Uri?) {
        viewModelScope.launch(IO) {

            if (uri == null) {
                _uiViewStat.emit(UiError(uiException.getErrorMessageInvalidFile()))
                return@launch
            }

            val absolutePath = createFileFromUri(uri)?.absolutePath

            if (!absolutePath.isNullOrBlank()) {
                val createdAt = PersianDate().time
                val id = title + absolutePath
                uploadingFileQueue.add(
                    AvanegarUploadingFileView(
                        id = id,
                        title = title,
                        filePath = absolutePath,
                        createdAt = createdAt,
                        uploadedPercent = 0f, //it will not store in db
                        isUploadingFinished = true, //it will not store in db
                    )
                )

                insertUploadingFileToDatabase(
                    id = id,
                    title = title,
                    filePath = absolutePath,
                    createdAt = createdAt,
                )

                if (uploadingFileQueue.isNotEmpty() && _isUploading.value != Uploading) {
                    _uiViewStat.emit(UiLoading)

                    _isUploading.value = Uploading
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
        createdAt: Long
    ) {
        repository.insertUploadingFile(
            AvanegarUploadingFileEntity(
                id = id,
                title = title,
                filePath = filePath,
                createdAt = createdAt
            )
        )
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
            repository.deleteUploadingFile(it)
        }
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }

    fun putDeniedPermissionToSharedPref(value: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(DENIED_PERMISSION_KEY, value)
            }
        }
    }

    fun hasDeniedPermissionPermanently(): Boolean {
        return sharedPref.getBoolean(DENIED_PERMISSION_KEY, false)
    }

    fun startUploading() {
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
                                uploadedPercent = (bytesUploaded.toDouble() / totalBytes).toFloat(),
                                isUploadingFinished = isDone
                            )
                        }
                    }

                    else -> it
                }
            }
        }
    }

    private suspend fun <T> handleResultState(
        result: AppResult<T>,
        onSuccess: ((Success<T>) -> Unit)? = null
    ) {
        when (result) {
            is Success -> {

                numberOfRequest = NUMBER_OF_REQUEST
                _uiViewStat.emit(UiSuccess)
                changeUploadFileToIdle()
                uploadingFileQueue = CopyOnWriteArrayList(
                    uploadingFileQueue.filter { it.id != result.data }
                )
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
                    _uiViewStat.emit(UiError(uiException.getErrorMessage(result.error)))
                    _isUploading.value = FailureUpload
                }
            }
        }
    }
}