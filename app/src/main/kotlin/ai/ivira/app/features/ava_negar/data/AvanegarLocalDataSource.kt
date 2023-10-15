package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.LastTrackFailure
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AvanegarLocalDataSource @Inject constructor(
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
            tracking = tracking,
            processed = processed.sortedByDescending { it.createdAt },
            uploading = uploading
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

    suspend fun insertProcessedFile(file: AvanegarProcessedFileEntity) =
        dao.insertProcessedFile(file)

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

    suspend fun updateLastTrackingFileFailure(lastTrackFailure: LastTrackFailure?) {
        dao.updateLastTrackingFileFailure(
            lastTrackFailure?.lastFailedRequest,
            lastTrackFailure?.lastTrackedBootElapsed
        )
    }
}