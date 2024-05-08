package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangArchiveFilesEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangCheckingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangUploadingFileEntity
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.HAMAHANG_FOLDER_PATH
import ai.ivira.app.utils.common.file.MP3_EXTENSION
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.db.ViraDb
import ai.ivira.app.utils.ui.combine
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class HamahangLocalDataSource @Inject constructor(
    private val db: ViraDb,
    private val dao: HamahangDao,
    private val fileOperationHelper: FileOperationHelper
) {
    fun getTrackingFiles(): Flow<List<HamahangTrackingFileEntity>> {
        return dao.getTrackingFiles()
    }

    fun getUploadingFiles(): Flow<List<HamahangUploadingFileEntity>> {
        return dao.getUploadingFiles()
    }

    fun getProcessedFiles(id: Int): Flow<HamahangProcessedFileEntity?> {
        return dao.getProcessedFile(id)
    }

    suspend fun insertProcessedFile(value: HamahangProcessedFileEntity) {
        dao.insertProcessedFile(value)
    }

    suspend fun insertUploadingFile(value: HamahangUploadingFileEntity) {
        dao.insertUploadingFile(value)
    }

    suspend fun insertTrackingFile(value: HamahangTrackingFileEntity) {
        dao.insertTrackingFile(value)
    }

    suspend fun insertCheckingFile(value: HamahangCheckingFileEntity) {
        dao.insertCheckingFile(value)
    }

    suspend fun insertTrackingFromUploading(
        uploadingId: String,
        tracking: HamahangTrackingFileEntity
    ) {
        db.withTransaction {
            dao.deleteUploadingFile(uploadingId)
            dao.insertTrackingFile(tracking)
        }
    }

    suspend fun insertProcessedFromTracking(token: String, fileUrl: String) {
        db.withTransaction {
            val tracking = dao.getTrackingFile(token)
            if (tracking != null) {
                val file = fileOperationHelper.getFile(
                    fileName = "${System.currentTimeMillis()}_${tracking.title}",
                    path = HAMAHANG_FOLDER_PATH,
                    extension = MP3_EXTENSION
                )

                dao.deleteTrackingFile(token)
                dao.insertProcessedFile(
                    HamahangProcessedFileEntity(
                        id = 0,
                        title = tracking.title,
                        fileUrl = fileUrl,
                        filePath = file.absolutePath,
                        inputFilePath = tracking.inputFilePath,
                        speaker = tracking.speaker,
                        createdAt = PersianDate().time,
                        isSeen = false
                    )
                )
            }
        }
    }

    suspend fun insertUploadingFromChecking(id: String, speaker: String) {
        db.withTransaction {
            val checking = dao.getCheckingFile(id)
            if (checking != null) {
                dao.deleteCheckingFile(id)
                dao.insertUploadingFile(
                    HamahangUploadingFileEntity(
                        id = "${System.currentTimeMillis()}_$speaker",
                        title = checking.title,
                        inputFilePath = checking.inputFilePath,
                        speaker = checking.speaker,
                        createdAt = PersianDate().time
                    )
                )
            }
        }
    }

    suspend fun updateIsProper(id: String, isProper: Boolean) {
        dao.updateIsProper(id, isProper)
    }

    suspend fun deleteCheckingFile(id: String) {
        dao.deleteCheckingFile(id)
    }

    suspend fun deleteProcessedFile(id: Int) {
        dao.deleteProcessedFile(id)
    }

    suspend fun deleteUploadingFile(id: String) {
        dao.deleteUploadingFile(id)
    }

    suspend fun deleteTrackingFile(token: String) {
        dao.deleteTrackingFile(token)
    }

    suspend fun markFileAsSeen(id: Int, isSeen: Boolean) {
        dao.markFileAsSeen(id, isSeen)
    }

    suspend fun updateTrackingFileLastFailure(time: TrackTime?) {
        dao.updateTrackingFileLastFailure(time?.systemTime, time?.bootTime)
    }

    suspend fun updateTitle(title: String, id: Int) {
        dao.updateTitle(title, id)
    }

    fun getAllFiles(): Flow<HamahangArchiveFilesEntity> {
        return combine(
            dao.getCheckingFiles(),
            dao.getProcessedFiles(),
            dao.getTrackingFiles(),
            dao.getUploadingFiles()
        ) { checkingFiles, processedFiles, trackingFiles, uploadingFiles ->
            HamahangArchiveFilesEntity(
                checking = checkingFiles,
                processed = processedFiles,
                tracking = trackingFiles,
                uploading = uploadingFiles
            )
        }
    }
}