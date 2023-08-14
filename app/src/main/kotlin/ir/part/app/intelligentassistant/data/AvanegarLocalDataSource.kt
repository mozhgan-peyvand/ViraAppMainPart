package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarUploadingFileEntity
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

    fun getAllArchiveFiles() = dao.getArchiveFiles().map {
        val tracking = mutableListOf<AvanegarTrackingFileEntity>()
        val processed = mutableListOf<AvanegarProcessedFileEntity>()
        val uploading = mutableListOf<AvanegarUploadingFileEntity>()

        it.forEach { archive ->
            if (archive.token.isNotBlank()) {
                tracking.add(archive.toAvanegarTrackingFileEntity())
            } else if (archive.filePath.isBlank() && archive.token.isBlank() && archive.text.isBlank()) {
                uploading.add(archive.toAvanegarUploadingFileEntity())
            } else {
                processed.add(archive.toAvanegarProcessedFileEntity())
            }
        }

        AvanegarArchiveFilesEntity(
            tracking = tracking,
            processed = processed,
            uploading = uploading
        )
    }

    suspend fun getUnprocessedFile(token: String) =
        dao.getUnprocessedFileDetail(token)

    suspend fun getUnprocessedFiles() = dao.getAllUnprocessedFiles()

    suspend fun insertUnprocessedFile(file: AvanegarTrackingFileEntity) =
        dao.insertUnprocessedFile(file)

    suspend fun insertProcessedFile(file: AvanegarProcessedFileEntity) =
        dao.insertProcessedFile(file)

    suspend fun insertUploadingFile(file: AvanegarUploadingFileEntity) =
        dao.insertUploadingFile(file)

    suspend fun deleteUnprocessedFile(token: String) =
        dao.deleteUnprocessedFile(token)

    fun getSearchResult(title: String) = dao.getSearch(title)

    suspend fun deleteProcessFile(id: Int?) = dao.deleteProcessedFile(id)

    suspend fun deleteUploadingFile(id: String) = dao.deleteUploadingFile(id)

    suspend fun updateTitle(title: String?, id: Int?) =
        dao.updateTitle(title = title, id = id)

    suspend fun editText(text: String, id: Int) = dao.editText(text, id)
}
