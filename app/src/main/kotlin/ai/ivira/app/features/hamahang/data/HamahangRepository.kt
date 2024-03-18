package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangArchiveFilesEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangUploadingFileEntity
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.file.toMultiPart
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.data.asPlainTextRequestBody
import android.os.SystemClock
import kotlinx.coroutines.flow.Flow
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HamahangRepository @Inject constructor(
    private val remoteDataSource: HamahangRemoteDataSource,
    private val localDataSource: HamahangLocalDataSource,
    private val fileOperationHelper: FileOperationHelper,
    private val networkHandler: NetworkHandler
) {
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

        return when (result) {
            is AppResult.Success -> {
                localDataSource.insertTrackingFromUploading(
                    uploadingId = id,
                    tracking = HamahangTrackingFileEntity(
                        token = result.data.token,
                        title = title,
                        inputFilePath = file.absolutePath,
                        speaker = speaker,
                        processEstimation = result.data.estimationTime,
                        insertAt = TrackTime(
                            systemTime = PersianDate().time,
                            bootTime = SystemClock.elapsedRealtime()
                        ),
                        lastFailure = null
                    )
                )
                AppResult.Success(result.data.token)
            }
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    suspend fun trackVoiceConversion(fileToken: String): AppResult<String> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(AppException.NetworkConnectionException())
        }

        val result = remoteDataSource.trackVoiceConversion(fileToken).toAppResult()

        return when (result) {
            is AppResult.Success -> {
                localDataSource.insertProcessedFromTracking(fileToken, result.data)
                AppResult.Success(result.data)
            }
            is AppResult.Error -> {
                localDataSource.updateTrackingFileLastFailure(
                    TrackTime(
                        systemTime = PersianDate().time,
                        bootTime = SystemClock.elapsedRealtime()
                    )
                )
                AppResult.Error(result.error)
            }
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

        return when (result) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }

    fun getTrackingFiles(): Flow<List<HamahangTrackingFileEntity>> {
        return localDataSource.getTrackingFiles()
    }

    fun getUploadingFiles(): Flow<List<HamahangUploadingFileEntity>> {
        return localDataSource.getUploadingFiles()
    }

    fun getProcessedFiles(id: Int): Flow<HamahangProcessedFileEntity?> {
        return localDataSource.getProcessedFiles(id)
    }

    suspend fun insertProcessedFile(value: HamahangProcessedFileEntity) {
        localDataSource.insertProcessedFile(value)
    }

    suspend fun insertUploadingFile(value: HamahangUploadingFileEntity) {
        localDataSource.insertUploadingFile(value)
    }

    suspend fun insertTrackingFile(value: HamahangTrackingFileEntity) {
        localDataSource.insertTrackingFile(value)
    }

    suspend fun deleteProcessedFile(id: Int, filePath: String) {
        localDataSource.deleteProcessedFile(id)
        runCatching {
            File(filePath).delete()
        }
    }

    suspend fun deleteTrackingFile(token: String) {
        localDataSource.deleteTrackingFile(token)
    }

    suspend fun deleteUploadingFile(id: String) {
        localDataSource.deleteUploadingFile(id)
    }

    suspend fun markFileAsSeen(id: Int, isSeen: Boolean) {
        localDataSource.markFileAsSeen(id, isSeen)
    }

    suspend fun updateTitle(title: String, id: Int) {
        localDataSource.updateTitle(title, id)
    }

    fun getArchiveFiles(): Flow<HamahangArchiveFilesEntity> {
        return localDataSource.getAllFiles()
    }
}