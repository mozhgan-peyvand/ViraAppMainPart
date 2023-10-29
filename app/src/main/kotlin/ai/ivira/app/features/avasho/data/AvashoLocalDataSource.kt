package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import javax.inject.Inject

class AvashoLocalDataSource @Inject constructor(
    private val dao: AvashoDao
) {
    suspend fun insertSpeechToDataBase(
        avashoFileEntity: AvashoProcessedFileEntity
    ) = dao.insertSpeechToDataBase(avashoFileEntity)
}