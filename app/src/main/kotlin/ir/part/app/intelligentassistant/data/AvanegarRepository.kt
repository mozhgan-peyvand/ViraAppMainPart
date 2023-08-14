package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarUploadingFileEntity
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import ir.part.app.intelligentassistant.utils.common.file.toMultiPart
import ir.part.app.intelligentassistant.utils.data.NetworkHandler
import ir.part.app.intelligentassistant.utils.data.api_result.AppException
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult.Error
import ir.part.app.intelligentassistant.utils.data.api_result.AppResult.Success
import ir.part.app.intelligentassistant.utils.data.api_result.toAppResult
import ir.part.app.intelligentassistant.utils.data.asPlainTextRequestBody
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject

class AvanegarRepository @Inject constructor(
    private val avanegarRemoteDataSource: AvanegarRemoteDataSource,
    private val avanegarLocalDataSource: AvanegarLocalDataSource,
    private val networkHandler: NetworkHandler
) {

    fun getArchiveFile(id: Int) =
        avanegarLocalDataSource.getArchiveFile(id)

    fun getAllArchiveFiles() =
        avanegarLocalDataSource.getAllArchiveFiles()

    fun getSearch(title: String) =
        avanegarLocalDataSource.getSearchResult(title)

    suspend fun audioToTextBelowSixtySecond(
        title: String,
        file: File,
        listener: UploadProgressCallback
    ): AppResult<Boolean> {

        val time = PersianDate().time
        return if (networkHandler.hasNetworkConnection()) {
            val result = avanegarRemoteDataSource.audioToTextBelowSixtySecond(
                multiPartFile = file.toMultiPart(title + time.toString(), listener),
                language = "fa".asPlainTextRequestBody
            ).toAppResult()

            when (result) {
                is Success -> {
                    avanegarLocalDataSource.insertProcessedFile(
                        AvanegarProcessedFileEntity(
                            id = 0,
                            title = title,
                            text = result.data,
                            createdAt = time, // TODO: improve
                            filePath = file.absolutePath,
                            isSeen = false
                        )
                    )

                    Success(true)
                }

                is Error -> Error(result.error)
            }
        } else Error(AppException.NetworkConnectionException())
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
                            token = result.data,
                            filePath = file.absolutePath,
                            title = title,
                            createdAt = PersianDate().time, // TODO: improve
                        )
                    )
                    Success(id)
                }

                is Error -> Error(result.error)
            }
        } else Error(AppException.NetworkConnectionException())
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

                is Error -> Error(result.error)
            }

        } else Error(AppException.NetworkConnectionException())
    }

    suspend fun deleteProcessFile(id: Int?) =
        avanegarLocalDataSource.deleteProcessFile(id)

    suspend fun updateTitle(title: String?, id: Int?) =
        avanegarLocalDataSource.updateTitle(title = title, id = id)

    suspend fun editText(text: String, id: Int) = avanegarLocalDataSource.editText(text, id)

    suspend fun insertUploadingFile(file: AvanegarUploadingFileEntity) =
        avanegarLocalDataSource.insertUploadingFile(file)
}