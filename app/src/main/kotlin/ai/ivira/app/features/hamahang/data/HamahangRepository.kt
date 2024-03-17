package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangArchiveFilesEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.file.toMultiPart
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.data.asPlainTextRequestBody
import ai.ivira.app.utils.ui.combine
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HamahangRepository @Inject constructor(
    private val remoteDataSource: HamahangRemoteDataSource,
    private val fileOperationHelper: FileOperationHelper,
    private val networkHandler: NetworkHandler,
    fakeData: HamahangFakeData
) {
    private val processedFiles = MutableStateFlow(fakeData.processedFiles)
    private val trackingFiles = MutableStateFlow(fakeData.trackingFiles)
    private val uploadingFiles = MutableStateFlow(fakeData.uploadingFiles)

    suspend fun voiceConversion(
        id: String,
        title: String,
        file: File,
        listener: UploadProgressCallback,
        speaker: String
    ): AppResult<String> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        val result = remoteDataSource.voiceConversion(
            multiPartFile = file.toMultiPart(id, listener),
            speaker = speaker.asPlainTextRequestBody
        ).toAppResult()

        // TODO implement database section
        return when (result) {
            is AppResult.Success -> AppResult.Success(result.data)
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    suspend fun trackVoiceConversion(fileToken: String): AppResult<String> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        val result = remoteDataSource.trackVoiceConversion(fileToken).toAppResult()

        // TODO implement database section
        return when (result) {
            is AppResult.Success -> AppResult.Success(result.data)
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    suspend fun downloadFile(
        id: Int,
        url: String,
        fileName: String,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        val result = remoteDataSource.downloadFile(url, File(fileName), progress).toAppResult()

        // TODO implement database section

        return when (result) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    fun getArchiveFile(id: Int): Flow<HamahangProcessedFileEntity?> {
        val flow = MutableStateFlow(processedFiles.value.find { it.id == id })
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            processedFiles.collect { flow.value = it.find { it.id == id } }
        }
        return flow.asStateFlow()
    }

    fun getArchiveFiles(): Flow<HamahangArchiveFilesEntity> {
        return combine(processedFiles, trackingFiles, uploadingFiles) { a, b, c ->
            HamahangArchiveFilesEntity(
                processed = a,
                tracking = b,
                uploading = c
            )
        }
    }

    suspend fun deleteProcessedFile(id: Int) {
        // todo delete from database and fileStorage
        delay(50)
        processedFiles.value = processedFiles.value.filter { it.id != id }.toMutableList()
    }

    suspend fun deleteTrackingFile(token: String) {
        delay(50)
        trackingFiles.value = trackingFiles.value.filter { it.token != token }.toMutableList()
    }

    suspend fun deleteUploadingFile(id: String) {
        delay(50)
        uploadingFiles.value = uploadingFiles.value.filter { it.id != id }.toMutableList()
    }
}