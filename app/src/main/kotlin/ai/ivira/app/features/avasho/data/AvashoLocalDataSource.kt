package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoArchiveFilesEntity
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val AVASHO_PROCESSED_ITEM = "processed"

class AvashoLocalDataSource @Inject constructor(
    private val dao: AvashoDao
) {
    fun getAllArchiveFiles() = dao.getArchiveFiles().map first@{ archives ->
        val processed = mutableListOf<AvashoProcessedFileEntity>()

        archives.forEach { archive ->
            when (archive.archiveType) {
                AVASHO_PROCESSED_ITEM -> {
                    processed.add(archive.toAvanegarProcessedFileEntity())
                }
            }
        }

        // TODO: do manual todo for all files
        // for some reason, sort was not applied with union, then we sort it manually
        AvashoArchiveFilesEntity(
            processed = processed.sortedByDescending { it.createdAt }
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
}