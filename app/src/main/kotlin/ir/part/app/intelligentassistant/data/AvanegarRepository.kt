package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import ir.part.app.intelligentassistant.utils.common.file.toMultiPart
import ir.part.app.intelligentassistant.utils.data.api_result.ApiResult
import ir.part.app.intelligentassistant.utils.data.asPlainTextRequestBody
import saman.zamani.persiandate.PersianDate
import java.io.File
import javax.inject.Inject

class AvanegarRepository @Inject constructor(
    private val avanegarRemoteDataSource: AvanegarRemoteDataSource,
    private val avanegarLocalDataSource: AvanegarLocalDataSource
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
    ): Result<Boolean> {

        val result = avanegarRemoteDataSource.audioToTextBelowSixtySecond(
            multiPartFile = file.toMultiPart(listener),
            language = "fa".asPlainTextRequestBody
        )

        if (result is ApiResult.Success) {
            avanegarLocalDataSource.insertProcessedFile(
                AvanegarProcessedFileEntity(
                    id = 0,
                    title = title,
                    text = result.data,
                    createdAt = PersianDate().time, // TODO: improve
                    filePath = file.absolutePath,
                    isSeen = false
                )
            )
        }
        // TODO: handle error

        // TODO: return error after parsing!!
        return Result.success(true)
    }

    suspend fun audioToTextAboveSixtySecond(
        title: String,
        file: File,
        listener: UploadProgressCallback
    ): Result<Boolean> {
        val result = avanegarRemoteDataSource.audioToTextAboveSixtySecond(
            multiPartFile = file.toMultiPart(listener),
            language = "fa".asPlainTextRequestBody
        )

        if (result is ApiResult.Success) {
            avanegarLocalDataSource.insertUnprocessedFile(
                AvanegarTrackingFileEntity(
                    token = result.data,
                    filePath = file.absolutePath,
                    title = title,
                    createdAt = PersianDate().time, // TODO: improve
                )
            )
        }
        // TODO: handle error

        // TODO: return error after parsing!!

        return Result.success(true)
    }

    suspend fun trackLargeFileResult(fileToken: String) {
        val result = avanegarRemoteDataSource.trackLargeFileResult(
            fileToken = fileToken
        )

        if (result is ApiResult.Success) {
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
        }
        // TODO: handle error

        // TODO: return error after parsing!!
    }

    suspend fun deleteProcessFile(id: Int?) =
        avanegarLocalDataSource.deleteProcessFile(id)

    suspend fun updateTitle(title: String?, id: Int?) =
        avanegarLocalDataSource.updateTitle(title = title, id = id)

    suspend fun editText(text: String, id: Int) = avanegarLocalDataSource.editText(text, id)
}