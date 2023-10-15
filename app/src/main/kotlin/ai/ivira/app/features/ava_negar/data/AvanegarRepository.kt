package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.LastTrackFailure
import ai.ivira.app.utils.common.file.UploadProgressCallback
import ai.ivira.app.utils.common.file.toMultiPart
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.AppResult.Error
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.data.api_result.toAppResult
import ai.ivira.app.utils.data.asPlainTextRequestBody
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt

class AvanegarRepository @Inject constructor(
    private val avanegarRemoteDataSource: AvanegarRemoteDataSource,
    private val avanegarLocalDataSource: AvanegarLocalDataSource,
    private val networkHandler: NetworkHandler
) {
    fun getArchiveFile(id: Int) =
        avanegarLocalDataSource.getArchiveFile(id)

    fun getAllArchiveFiles() =
        avanegarLocalDataSource.getAllArchiveFiles()

    fun getTrackingFiles() = avanegarLocalDataSource.getTrackingFiles()

    suspend fun getAllFilePaths() = avanegarLocalDataSource.getAllFilePaths()

    suspend fun getTrackingFilesSync() = avanegarLocalDataSource.getTrackingFilesSync()

    suspend fun getSearch(query: String) = avanegarLocalDataSource.getSearchResult(query)

    suspend fun audioToTextBelowSixtySecond(
        id: String,
        title: String,
        file: File,
        listener: UploadProgressCallback
    ): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avanegarRemoteDataSource.audioToTextBelowSixtySecond(
                multiPartFile = file.toMultiPart(id, listener),
                language = "fa".asPlainTextRequestBody
            ).toAppResult()

            when (result) {
                is Success -> {
                    avanegarLocalDataSource.deleteUploadingFile(id)
                    avanegarLocalDataSource.insertProcessedFile(
                        AvanegarProcessedFileEntity(
                            id = 0,
                            title = title,
                            text = result.data,
                            createdAt = PersianDate().time, // TODO: improve,
                            filePath = file.absolutePath,
                            isSeen = false
                        )
                    )

                    Success(id)
                }

                is Error -> Error(result.error)
            }
        } else {
            Error(AppException.NetworkConnectionException())
        }
    }

    suspend fun audioToTextAboveSixtySecond(
        id: String,
        title: String,
        file: File,
        listener: UploadProgressCallback
    ): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avanegarRemoteDataSource.audioToTextAboveSixtySecond(
                multiPartFile = file.toMultiPart(id, listener),
                language = "fa".asPlainTextRequestBody
            ).toAppResult()

            when (result) {
                is Success -> {
                    avanegarLocalDataSource.deleteUploadingFile(id)
                    avanegarLocalDataSource.insertUnprocessedFile(
                        AvanegarTrackingFileEntity(
                            token = result.data.token,
                            processEstimation = result.data.processEstimation?.roundToInt(),
                            filePath = file.absolutePath,
                            title = title,
                            createdAt = PersianDate().time, // TODO: improve
                            bootElapsedTime = SystemClock.elapsedRealtime(),
                            lastFailure = null
                        )
                    )
                    Success(id)
                }

                is Error -> Error(result.error)
            }
        } else {
            Error(AppException.NetworkConnectionException())
        }
    }

    suspend fun trackLargeFileResult(fileToken: String): AppResult<Boolean> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avanegarRemoteDataSource.trackLargeFileResult(
                fileToken = fileToken
            ).toAppResult()

            when (result) {
                is Success -> {
                    val tracked = avanegarLocalDataSource.getUnprocessedFile(fileToken)
                    if (tracked != null) {
                        avanegarLocalDataSource.deleteUnprocessedFile(fileToken)
                        avanegarLocalDataSource.insertProcessedFile(
                            AvanegarProcessedFileEntity(
                                id = 0,
                                title = tracked.title,
                                text = result.data,
                                createdAt = PersianDate().time, // TODO: improve,
                                filePath = tracked.filePath,
                                isSeen = false
                            )
                        )
                    }

                    Success(true)
                }

                is Error -> {
                    avanegarLocalDataSource.updateLastTrackingFileFailure(
                        LastTrackFailure(
                            lastFailedRequest = PersianDate().time,
                            lastTrackedBootElapsed = SystemClock.elapsedRealtime()
                        )
                    )
                    Error(result.error)
                }
            }
        } else {
            Error(AppException.NetworkConnectionException())
        }
    }

    suspend fun deleteProcessFile(id: Int?) =
        avanegarLocalDataSource.deleteProcessFile(id)

    suspend fun deleteUnprocessedFile(id: String) =
        avanegarLocalDataSource.deleteUnprocessedFile(id)

    suspend fun deleteUploadingFile(id: String) =
        avanegarLocalDataSource.deleteUploadingFile(id)

    suspend fun updateTitle(title: String?, id: Int?) =
        avanegarLocalDataSource.updateTitle(title = title, id = id)

    suspend fun editText(text: String, id: Int) = avanegarLocalDataSource.editText(text, id)

    suspend fun insertUploadingFile(file: AvanegarUploadingFileEntity) =
        avanegarLocalDataSource.insertUploadingFile(file)
}