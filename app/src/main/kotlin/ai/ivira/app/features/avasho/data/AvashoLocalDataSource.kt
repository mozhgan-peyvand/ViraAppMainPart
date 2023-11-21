package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val AVASHO_PROCESSED_ITEM = "processed"
const val AVASHO_TRACKING_ITEM = "tracking"
const val AVASHO_UPLOADING_ITEM = "uploading"

class AvashoLocalDataSource @Inject constructor(
    private val dao: AvashoDao
) {
    fun getAllArchiveFiles() = dao.getArchiveFiles().map first@{ archives ->
        val tracking = mutableListOf<AvashoTrackingFileEntity>()
        val processed = mutableListOf<AvashoProcessedFileEntity>()
        val uploading = mutableListOf<AvashoUploadingFileEntity>()

        archives.forEach { archive ->
            when (archive.archiveType) {
                AVASHO_TRACKING_ITEM -> {
                    tracking.add(archive.toAvashoTrackingFileEntity())
                }
                AVASHO_PROCESSED_ITEM -> {
                    processed.add(archive.toAvanegarProcessedFileEntity())
                }
                AVASHO_UPLOADING_ITEM -> {
                    uploading.add(archive.toAvashoUploadingFileEntity())
                }
            }
        }

        // TODO: do manual todo for all files
        // for some reason, sort was not applied with union, then we sort it manually
        AvashoArchiveFilesEntity(
            tracking = tracking.sortedByDescending { it.createdAt },
            processed = processed.sortedByDescending { it.createdAt },
            uploading = uploading.sortedBy { it.createdAt }
        )
    }.flowOn(Dispatchers.IO)

    suspend fun insertProcessedSpeechToDataBase(
        avashoFileEntity: AvashoProcessedFileEntity
    ) = dao.insertProcessedSpeechToDataBase(avashoFileEntity)

    suspend fun updateFilePath(id: Int, filePath: String) {
        dao.updateFilePath(id, filePath)
    }

    suspend fun updateDownloadStatus(id: Int, isDownloading: Boolean) {
        dao.updateDownloadStatus(id, isDownloading)
    }

    suspend fun insertUploadingSpeechToDatabase(avashoUploadingFileEntity: AvashoUploadingFileEntity) {
        dao.insertUploadingSpeechToDatabase(avashoUploadingFileEntity)
    }

    suspend fun insertTrackingSpeechToDatabase(avashoTrackingFileEntity: AvashoTrackingFileEntity) {
        dao.insertTrackingSpeechToDatabase(avashoTrackingFileEntity)
    }

    suspend fun deleteUploadingFile(id: String) {
        dao.deleteUploadingFile(id)
    }

    suspend fun updateTitle(title: String, id: Int) {
        dao.updateTitle(title, id)
    }

    suspend fun deleteProcessFile(id: Int) {
        dao.deleteProcessedFile(id)
    }
}