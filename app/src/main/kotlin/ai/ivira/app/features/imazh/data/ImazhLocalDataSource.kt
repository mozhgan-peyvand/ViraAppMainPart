package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhArchiveFilesEntity
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedFileEntity
import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import ai.ivira.app.utils.common.file.FileOperationHelper
import ai.ivira.app.utils.common.file.IMAZH_FOLDER_PATH
import ai.ivira.app.utils.common.file.PNG_EXTENSION
import ai.ivira.app.utils.data.db.ViraDb
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import saman.zamani.persiandate.PersianDate
import java.util.UUID
import javax.inject.Inject

const val Imazh_PROCESSED_ITEM = "processed"
const val Imazh_TRACKING_ITEM = "tracking"

class ImazhLocalDataSource @Inject constructor(
    private val db: ViraDb,
    private val dao: ImazhDao,
    private val fileOperationHelper: FileOperationHelper
) {
    fun getTrackingFiles() = dao.getTrackingFiles()
    fun getRecentHistory() = dao.getRecentHistory()

    fun getAllArchiveFiles() = dao.getArchiveFiles().map first@{ archives ->
        val tracking = mutableListOf<ImazhTrackingFileEntity>()
        val processed = mutableListOf<ImazhProcessedFileEntity>()

        archives.forEach { archive ->
            when (archive.archiveType) {
                Imazh_PROCESSED_ITEM -> {
                    processed.add(archive.toImazhProcessedFileEntity())
                }

                Imazh_TRACKING_ITEM -> {
                    tracking.add(archive.toImazhTrackingFileEntity())
                }
            }
        }

        ImazhArchiveFilesEntity(
            processed = processed.sortedByDescending { it.createdAt },
            tracking = tracking.sortedByDescending { it.insertAt.systemTime }
        )
    }.flowOn(IO)

    suspend fun addPromptToHistory(item: ImazhHistoryEntity) {
        dao.addPromptToHistory(item)
    }

    suspend fun insertTrackingFile(value: ImazhTrackingFileEntity) {
        dao.insertTrackingFile(value)
    }

    suspend fun insertProcessedFromTracking(token: String, imagePath: String) {
        val file = fileOperationHelper.getFile(
            fileName = "${System.currentTimeMillis()}_${UUID.randomUUID().mostSignificantBits}",
            path = IMAZH_FOLDER_PATH,
            extension = PNG_EXTENSION
        )

        db.withTransaction {
            val tracking = dao.getTrackingFile(token)
            if (tracking != null) {
                dao.deleteTrackingFile(token)
                dao.insertProcessedFile(
                    ImazhProcessedFileEntity(
                        id = 0,
                        imagePath = imagePath,
                        filePath = file.absolutePath,
                        keywords = tracking.keywords,
                        prompt = tracking.prompt,
                        englishPrompt = tracking.englishPrompt,
                        negativePrompt = tracking.negativePrompt,
                        englishNegativePrompt = tracking.englishNegativePrompt,
                        style = tracking.style,
                        createdAt = PersianDate().time
                    )
                )
            }
        }
    }

    fun getAllProcessedFiles() = dao.getAllProcessedFiles()

    fun getPhotoInfo(id: Int) = dao.getPhotoInfo(id)

    suspend fun deleteProcessedFile(id: Int) = dao.deleteProcessedFile(id)

    suspend fun deleteTrackingFile(token: String) = dao.deleteTrackingFile(token)

    suspend fun updateFilePath(id: Int, filePath: String) {
        dao.updateFilePath(id, filePath)
    }
}