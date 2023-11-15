package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.utils.common.file.AVASHO_FOLDER_PATH
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException.NetworkConnectionException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject
const val Base_Url = "https://partai.gw.isahab.ir"
class AvashoRepository @Inject constructor(
    private val avashoRemoteDataSource: AvashoRemoteDataSource,
    private val avashoLocalDataSource: AvashoLocalDataSource,
    private val fileOperationHelper: FileOperationHelper,
    private val networkHandler: NetworkHandler
) {
    fun getAllArchiveFiles() =
        avashoLocalDataSource.getAllArchiveFiles()

    suspend fun convertToSpeechShort(
        id: String,
        text: String,
        speakerType: String,
        fileName: String
    ): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avashoRemoteDataSource.getSpeechFile(
                text = text,
                speakerType = speakerType
            ).toAppResult()

            when (result) {
                is AppResult.Success -> {
                    avashoLocalDataSource.deleteUploadingFile(id)
                    avashoLocalDataSource.insertProcessedSpeechToDataBase(
                        AvashoProcessedFileEntity(
                            id = 0,
                            checksum = result.data.checksum,
                            fileUrl = Base_Url + result.data.filePath,
                            fileName = fileName,
                            filePath = "",
                            text = text,
                            createdAt = PersianDate().time,
                            isDownloading = false
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
                    avashoLocalDataSource.deleteUploadingFile(id)
                    avashoLocalDataSource.insertTrackingSpeechToDatabase(
                        AvashoTrackingFileEntity(
                            token = result.data.token,
                            processEstimation = result.data.estimatedProcessTime
                                .filter { it.isDigit() }
                                .toInt(),
                            createdAt = PersianDate().time,
                            title = fileName,
                            bootElapsedTime = SystemClock.elapsedRealtime(),
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

    suspend fun insertUploadingSpeechToDatabase(avashoUploadingFileEntity: AvashoUploadingFileEntity) {
        avashoLocalDataSource.insertUploadingSpeechToDatabase(avashoUploadingFileEntity)
    }

    suspend fun downloadFile(
        id: Int,
        url: String,
        fileName: String,
        progress: (byteReceived: Long, totalSize: Long) -> Unit
    ) {
        val file = fileOperationHelper.getFile(
            fileName = fileName,
            path = AVASHO_FOLDER_PATH
        )
        avashoRemoteDataSource.downloadFile(url, file, progress)

        if (file.exists()) {
            avashoLocalDataSource.updateFilePath(id, file.absolutePath)
            avashoLocalDataSource.updateDownloadStatus(id, false)
        }
    }
}