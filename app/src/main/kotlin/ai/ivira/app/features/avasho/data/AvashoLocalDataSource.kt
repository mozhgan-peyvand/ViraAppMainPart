package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.db.ViraDb
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

const val AVASHO_PROCESSED_ITEM = "processed"
const val AVASHO_TRACKING_ITEM = "tracking"
const val AVASHO_UPLOADING_ITEM = "uploading"

class AvashoLocalDataSource @Inject constructor(
    private val db: ViraDb,
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
            tracking = tracking.sortedByDescending { it.insertAt.systemTime },
            processed = processed.sortedByDescending { it.createdAt },
            uploading = uploading.sortedBy { it.createdAt }
        )
    }.flowOn(Dispatchers.IO)

    fun getTrackingFiles() = dao.getTrackingFiles()

    suspend fun insertProcessedFromUploading(
        uploadingId: String,
        fileUrl: String,
        text: String,
        fileName: String
    ) {
        db.withTransaction {
            dao.deleteUploadingFile(uploadingId)
            dao.insertProcessed(
                AvashoProcessedFileEntity(
                    id = 0,
                    fileUrl = fileUrl,
                    fileName = fileName,
                    filePath = "",
                    text = text,
                    createdAt = PersianDate().time,
                    isDownloading = false
                )
            )
        }
    }

    suspend fun insertProcessedFromTracking(token: String, fileUrl: String) {
        db.withTransaction {
            val tracking = dao.getTrackingFile(token)
            if (tracking != null) {
                dao.deleteTrackingFile(token)
                dao.insertProcessed(
                    AvashoProcessedFileEntity(
                        id = 0,
                        fileUrl = fileUrl,
                        filePath = "",
                        fileName = tracking.title,
                        text = tracking.text,
                        createdAt = PersianDate().time,
                        isDownloading = false
                    )
                )
            }
        }
    }

    suspend fun updateFilePath(id: Int, filePath: String) {
        dao.updateFilePath(id, filePath)
    }

    suspend fun updateDownloadStatus(id: Int, isDownloading: Boolean) {
        dao.updateDownloadStatus(id, isDownloading)
    }

    suspend fun updateTrackingFileLastFailure(time: TrackTime?) {
        dao.updateTrackingFileLastFailure(time?.systemTime, time?.bootTime)
    }

    suspend fun insertUploadingSpeech(avashoUploadingFileEntity: AvashoUploadingFileEntity) {
        dao.insertUploadingSpeech(avashoUploadingFileEntity)
    }

    suspend fun insertTrackingFromUploading(
        uploadingId: String,
        tracking: AvashoTrackingFileEntity
    ) {
        db.withTransaction {
            dao.deleteUploadingFile(uploadingId)
            dao.insertTrackingSpeech(tracking)
        }
    }

    suspend fun updateTitle(title: String, id: Int) {
        dao.updateTitle(title, id)
    }

    suspend fun deleteProcessFile(id: Int) {
        dao.deleteProcessedFile(id)
    }

    suspend fun searchAvashoArchiveItem(searchText: String) =
        dao.searchAvashoArchiveItem(searchText)
}