package ir.part.app.intelligentassistant.ui.screen.archive

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.data.AvanegarRepository
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.toAvanegarTrackingFileView
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEventPublisher
import ir.part.app.intelligentassistant.utils.common.file.FileCache
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class AvaNegarArchiveViewModel @Inject constructor(
    private val repository: AvanegarRepository,
    private val fileCache: FileCache,
    private val aiEventPublisher: IntelligentAssistantEventPublisher
) : ViewModel() {

    private val _uploadFileState: MutableState<UploadFileStatus> = mutableStateOf(UploadIdle)
    val uploadFileState: State<UploadFileStatus> = _uploadFileState

    private val _aiEvent: MutableState<IntelligentAssistantEvent?> = mutableStateOf(null)
    val aiEvent: State<IntelligentAssistantEvent?> = _aiEvent

    private val _allArchiveFiles: MutableState<List<ArchiveView>> = mutableStateOf(listOf())
    val allArchiveFiles: State<List<ArchiveView>> = _allArchiveFiles

    //TODO set appropriate name
    var isSavingFile = false
        private set

    private var files: MutableMap<Uri, File> = ConcurrentHashMap()

    var job: Job? = null

    init {
        viewModelScope.launch {
            repository.getAllArchiveFiles().collect { avanegarArchiveFile ->
                val processedList = avanegarArchiveFile.processed
                val trackingList = avanegarArchiveFile.tracking
                _allArchiveFiles.value =
                    trackingList.map { it.toAvanegarTrackingFileView() } + processedList.map { it.toAvanegarProcessedFileView() }
            }
        }

        viewModelScope.launch {
            aiEventPublisher.events.collect {
                _aiEvent.value = it
            }
        }
    }

    fun uploadFile(
        title: String,
        uri: Uri?,
        listener: UploadProgressCallback
    ) {
        _uploadFileState.value = UploadInProgress

        //todo check the duration of file and use the correct one
        audioToTextAboveSixtySecond(title, uri, listener)
    }


    private fun audioToTextBelowSixtySecond(
        title: String,
        uri: Uri?,
        listener: UploadProgressCallback
    ) {
        job?.cancel()

        job = viewModelScope.launch(Dispatchers.IO) {
            val file = createFileFromUri(uri!!) // todo show error if uri is null
            // todo: show error when file is null
            if (file != null) {
                val result = repository.audioToTextBelowSixtySecond(title, file, listener)

                if (result.isSuccess) {
                    _uploadFileState.value = UploadSuccess
                    changeUploadFileToIdle()
                } else _uploadFileState.value = UploadFailure
            }
        }
    }

    private fun audioToTextAboveSixtySecond(
        title: String,
        uri: Uri?,
        listener: UploadProgressCallback
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val file = createFileFromUri(uri!!) // todo show error if uri is null
            // todo: show error when file is null
            if (file != null) {
                val result = repository.audioToTextAboveSixtySecond(
                    title, file, listener
                )

                if (result.isSuccess) {
                    _uploadFileState.value = UploadSuccess
                    changeUploadFileToIdle()
                } else _uploadFileState.value = UploadFailure
            }

        }
    }

    private fun changeUploadFileToIdle() {
        viewModelScope.launch {

            //fixme set the correct value
            delay(2000)
            _uploadFileState.value = UploadIdle
        }
    }

    private suspend fun createFileFromUri(uri: Uri): File? {
        isSavingFile = true

        return files[uri] ?: fileCache.cacheUri(uri)?.also { file ->
            files[uri] = file
            isSavingFile = false
        }
    }

    fun cancelDownload() {
        job?.cancel()
        _uploadFileState.value = UploadIdle
    }

    fun updateIsSaving(value: Boolean) {
        isSavingFile = value
    }

    fun removeFile(id: Int?) = viewModelScope.launch {
        repository.deleteProcessFile(id)
    }

    fun updateTitle(title: String?, id: Int?) = viewModelScope.launch {
        repository.updateTitle(title = title, id = id)
    }
}