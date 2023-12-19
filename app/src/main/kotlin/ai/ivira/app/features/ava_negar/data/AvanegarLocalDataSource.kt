package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.utils.data.TrackTime
import ai.ivira.app.utils.data.db.ViraDb
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class AvanegarLocalDataSource @Inject constructor(
    private val db: ViraDb,
    private val dao: AvanegarDao
) {
    fun getArchiveFile(id: Int) = dao.getProcessedFileDetail(id).onEach {
        it?.let {
            if (!it.isSeen) {
                dao.markFileAsSeen(it.id)
            }
        }
    }.flowOn(IO)

    fun getAllArchiveFiles() = dao.getArchiveFiles().map first@{ archives ->
        val tracking = mutableListOf<AvanegarTrackingFileEntity>()
        val processed = mutableListOf<AvanegarProcessedFileEntity>()
        val uploading = mutableListOf<AvanegarUploadingFileEntity>()

        archives.forEach { archive ->
            when (archive.archiveType) {
                TRACKING_ITEM -> {
                    tracking.add(archive.toAvanegarTrackingFileEntity())
                }

                UPLOADING_ITEM -> {
                    uploading.add(archive.toAvanegarUploadingFileEntity())
                }

                PROCESSED_ITEM -> {
                    processed.add(archive.toAvanegarProcessedFileEntity())
                }
            }
        }

        // TODO: do manual todo for all files
        // for some reason, sort was not applied with union, then we sort it manually
        AvanegarArchiveFilesEntity(
            tracking = tracking.sortedByDescending { it.insertAt.systemTime },
            processed = processed.sortedByDescending { it.createdAt },
            uploading = uploading.sortedBy { it.createdAt }
        )
    }.flowOn(IO)

    suspend fun getAllFilePaths() = dao.getAllFilePaths()

    suspend fun getUnprocessedFile(token: String) =
        dao.getUnprocessedFileDetail(token)

    fun getTrackingFiles() = dao.getTrackingFiles()

    suspend fun getTrackingFilesSync() = dao.getTrackingFilesSync()

    suspend fun insertUnprocessedFile(file: AvanegarTrackingFileEntity) {
        dao.insertUnprocessedFile(file)
    }

    suspend fun insertProcessedFromTracking(token: String, text: String) = db.withTransaction {
        val tracked = dao.getUnprocessedFileDetail(token)
        if (tracked != null) {
            dao.deleteUnprocessedFile(token)
            dao.insertProcessedFile(
                AvanegarProcessedFileEntity(
                    id = 0,
                    title = tracked.title,
                    text = text,
                    createdAt = PersianDate().time,
                    filePath = tracked.filePath,
                    isSeen = false
                )
            )
        }
    }

    suspend fun insertProcessedFromUploading(
        uploadingId: String,
        title: String,
        text: String,
        filePath: String
    ) {
        db.withTransaction {
            dao.deleteUploadingFile(uploadingId)
            dao.insertProcessedFile(
                AvanegarProcessedFileEntity(
                    id = 0,
                    title = title,
                    text = text,
                    createdAt = PersianDate().time, // TODO: improve,
                    filePath = filePath,
                    isSeen = false
                )
            )
        }
    }

    suspend fun insertUploadingFile(file: AvanegarUploadingFileEntity) {
        dao.insertUploadingFile(file)
    }

    suspend fun deleteUnprocessedFile(token: String) =
        dao.deleteUnprocessedFile(token)

    suspend fun getSearchResult(query: String) = dao.getSearch(query)

    suspend fun deleteProcessFile(id: Int?) = dao.deleteProcessedFile(id)

    suspend fun deleteUploadingFile(id: String) = dao.deleteUploadingFile(id)

    suspend fun updateTitle(title: String?, id: Int?) =
        dao.updateTitle(title = title, id = id)

    suspend fun editText(text: String, id: Int) = dao.editText(text, id)

    suspend fun updateLastTrackingFileFailure(time: TrackTime?) {
        dao.updateLastTrackingFileFailure(
            time?.systemTime,
            time?.bootTime
        )
    }
}