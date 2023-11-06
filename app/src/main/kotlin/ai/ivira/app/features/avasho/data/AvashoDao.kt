package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoArchiveUnionEntity
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AvashoDao {
    @Query(
        """
        SELECT * FROM (
            SELECT 0 AS id, '' AS uploadingId, token, '' AS text, title AS fileName, createdAt, '' AS fileUrl, '' AS filePath, '' AS checksum, 0 AS isDownloading, '' AS speaker, 'tracking' as archiveType,
            processEstimation, bootElapsedTime, lastFailedRequest, lastTrackedBootElapsed
           FROM AvashoTrackingFileEntity
        )
        UNION
        
        SELECT * FROM (
            SELECT id, '' AS uploadingId,'' AS token, text, fileName, createdAt, fileUrl, filePath,  checksum, isDownloading, '' AS speaker, 'processed' as archiveType,
            0 AS processEstimation, 0 AS bootElapsedTime, 0 AS lastFailedRequest, 0 AS lastTrackedBootElapsed       
            FROM AvashoProcessedFileEntity
        )
        UNION
        
        SELECT * FROM (
            SELECT 0 AS id, id AS uploadingId, '' AS Token, text, title AS fileName, createdAt, '' AS fileUrl, '' AS filePath, '' AS checksum, 0 AS isDownloading, speaker, 'uploading' as archiveType,
            0 AS processEstimation, 0 AS bootElapsedTime, 0 AS lastFailedRequest, 0 AS lastTrackedBootElapsed
            FROM AvashoUploadingFileEntity
        )
        
    """
    )
    fun getArchiveFiles(): Flow<List<AvashoArchiveUnionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedSpeechToDataBase(file: AvashoProcessedFileEntity)

    @Query("UPDATE AvashoProcessedFileEntity SET filePath=:filePath WHERE id=:id")
    suspend fun updateFilePath(id: Int, filePath: String)

    @Query("UPDATE AvashoProcessedFileEntity SET isDownloading=:isDownloading WHERE id=:id")
    suspend fun updateDownloadStatus(id: Int, isDownloading: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadingSpeechToDatabase(
        avashoUploadingFileEntity: AvashoUploadingFileEntity
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingSpeechToDatabase(
        avashoTrackingFileEntity: AvashoTrackingFileEntity
    )

    @Query("DELETE FROM AvashoUploadingFileEntity WHERE id =:id")
    suspend fun deleteUploadingFile(id: String)
}