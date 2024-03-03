package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.utils.common.file.AVASHO_FOLDER_PATH
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.MP3_EXTENSION
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.api_result.ApiResult
import ai.ivira.app.utils.data.api_result.AppException.NetworkConnectionException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppException
import ai.ivira.app.utils.data.api_result.toAppResult
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class AvashoRepository @Inject constructor(
    private val avashoRemoteDataSource: AvashoRemoteDataSource,
    private val avashoLocalDataSource: AvashoLocalDataSource,
    private val fileOperationHelper: FileOperationHelper,
    private val networkHandler: NetworkHandler
) {
    init {
        System.loadLibrary("vira")
    }

    suspend fun markFileAsSeen(id: Int) = avashoLocalDataSource.markFileAsSeen(id)

    fun getAllArchiveFiles() = avashoLocalDataSource.getAllArchiveFiles()

    fun getTrackingFiles() = avashoLocalDataSource.getTrackingFiles()

    suspend fun convertToSpeechShort(
        id: String,
        text: String,
        speakerType: String,
        fileName: String
    ): AppResult<String> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(NetworkConnectionException())
        }
        val result = avashoRemoteDataSource.getSpeechFile(
            text = text,
            speakerType = speakerType
        ).toAppResult()

        return when (result) {
            is AppResult.Success -> {
                avashoLocalDataSource.insertProcessedFromUploading(
                    uploadingId = id,
                    fileUrl = "${bu()}${result.data.filePath}",
                    text = text,
                    fileName = fileName
                )
                AppResult.Success(fileName)
            }
            is AppResult.Error -> {
                AppResult.Error(result.error)
            }
        }
    }

    suspend fun convertToSpeechLong(
        id: String,
        text: String,
        speakerType: String,
        fileName: String
    ): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avashoRemoteDataSource.getSpeechFileLong(
                text = text,
                speakerType = speakerType
            ).toAppResult()

            when (result) {
                is AppResult.Success -> {
                    avashoLocalDataSource.insertTrackingFromUploading(
                        uploadingId = id,
                        tracking = AvashoTrackingFileEntity(
                            token = result.data.token,
                            processEstimation = result.data.estimatedProcessTime
                                .filter { it.isDigit() }
                                .toIntOrNull(),
                            title = fileName,
                            text = text,
                            insertAt = TrackTime(
                                systemTime = PersianDate().time,
                                bootTime = SystemClock.elapsedRealtime()
                            ),
                            lastFailure = null
                        )
                    )
                    AppResult.Success(fileName)
                }
                is AppResult.Error -> {
                    AppResult.Error(result.error)
                }
            }
        } else {
            AppResult.Error(NetworkConnectionException())
        }
    }

    suspend fun insertUploadingSpeech(avashoUploadingFileEntity: AvashoUploadingFileEntity) {
        avashoLocalDataSource.insertUploadingSpeech(avashoUploadingFileEntity)
    }

    suspend fun trackLargeTextResult(fileToken: String): AppResult<Unit> {
        if (!networkHandler.hasNetworkConnection()) {
            return AppResult.Error(NetworkConnectionException())
        }

        return when (val result = avashoRemoteDataSource.trackLargeTextResult(fileToken)) {
            is ApiResult.Success -> {
                avashoLocalDataSource.insertProcessedFromTracking(
                    token = fileToken,
                    fileUrl = "${bu()}${result.data}"
                )
                AppResult.Success(Unit)
            }
            is ApiResult.Error -> {
                avashoLocalDataSource.updateTrackingFileLastFailure(
                    TrackTime(
                        systemTime = PersianDate().time,
                        bootTime = SystemClock.elapsedRealtime()
                    )
                )
                AppResult.Error(result.error.toAppException())
            }
        }
    }

    suspend fun downloadFile(
        id: Int,
        url: String,
        fileName: String,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ): AppResult<Unit> {
        return if (networkHandler.hasNetworkConnection()) {
            val file = fileOperationHelper.getFile(
                fileName = "${System.currentTimeMillis()}_$fileName",
                path = AVASHO_FOLDER_PATH,
                extension = MP3_EXTENSION
            )

            val result = avashoRemoteDataSource.downloadFile(url, file, progress).toAppResult()

            if (file.exists()) {
                avashoLocalDataSource.updateFilePath(id, file.absolutePath)
                avashoLocalDataSource.updateDownloadStatus(id, false)
            }

            when (result) {
                is AppResult.Success -> AppResult.Success(Unit)
                is AppResult.Error -> AppResult.Error(result.error)
            }
        } else {
            AppResult.Error(NetworkConnectionException())
        }
    }

    suspend fun updateTitle(title: String, id: Int) {
        avashoLocalDataSource.updateTitle(title, id)
    }

    suspend fun deleteProcessFile(id: Int) {
        avashoLocalDataSource.deleteProcessFile(id)
    }

    suspend fun removeUploadingFile(id: String) {
        avashoLocalDataSource.removeUploadingFile(id)
    }

    suspend fun removeTrackingFile(token: String) {
        avashoLocalDataSource.removeTrackingFile(token)
    }

    suspend fun searchAvashoArchiveItem(searchText: String) =
        avashoLocalDataSource.searchAvashoArchiveItem(searchText)

    suspend fun checkSpeech(speech: String): AppResult<Boolean> =
        avashoRemoteDataSource.checkSpeech(speech).toAppResult()

    private external fun bu(): String
}