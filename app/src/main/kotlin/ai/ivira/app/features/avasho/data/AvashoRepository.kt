package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.utils.data.NetworkHandler
import ai.ivira.app.utils.data.api_result.AppException.NetworkConnectionException
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.data.api_result.toAppResult
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class AvashoRepository @Inject constructor(
    private val avashoRemoteDataSource: AvashoRemoteDataSource,
    private val avashoLocalDataSource: AvashoLocalDataSource,
    private val networkHandler: NetworkHandler
) {
    suspend fun convertToSpeechBelow1000(
        text: String,
        speakerType: String,
        fileName: String
    ): AppResult<String> {
        return if (networkHandler.hasNetworkConnection()) {
            val result = avashoRemoteDataSource.getSpeechFile(
                text = text,
                speakerType = speakerType
            )
                .toAppResult()

            when (result) {
                is AppResult.Success -> {
                    avashoLocalDataSource.insertSpeechToDataBase(
                        AvashoProcessedFileEntity(
                            id = 0,
                            checksum = result.data.data.checksum,
                            filePath = result.data.data.filePath,
                            fileName = fileName,
                            text = text,
                            createdAt = PersianDate().time
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
}