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
            SELECT 0 AS id, '' AS uploadingId, token, text, title AS fileName, insertSystemTime, 
            insertBootTime, '' AS fileUrl, '' AS filePath, 0 AS isDownloading, '' AS speaker,
            'tracking' as archiveType, processEstimation, lastFailureSystemTime, lastFailureBootTime
           FROM AvashoTrackingFileEntity
        )
        UNION
        
        SELECT * FROM (
            SELECT id, '' AS uploadingId, '' AS token, text, fileName, createdAt, 0 AS insertBootTime,
            fileUrl, filePath, isDownloading, '' AS speaker, 'processed' as archiveType, 
            0 AS processEstimation, 0 AS lastFailureSystemTime, 0 AS lastFailureBootTime       
            FROM AvashoProcessedFileEntity
        )
        UNION
        
        SELECT * FROM (
            SELECT 0 AS id, id AS uploadingId, '' AS token, text, title AS fileName, createdAt, 
            0 AS insertBootTime, '' AS fileUrl, '' AS filePath, 0 AS isDownloading, speaker, 
            'uploading' as archiveType, 0 AS processEstimation, 0 AS lastFailureSystemTime, 0 AS lastTrackedBootElapsed
            FROM AvashoUploadingFileEntity
        )
        
    """
    )
    fun getArchiveFiles(): Flow<List<AvashoArchiveUnionEntity>>

    @Query("SELECT * FROM AvashoTrackingFileEntity")
    fun getTrackingFiles(): Flow<List<AvashoTrackingFileEntity>>

    @Query("SELECT * FROM AvashoTrackingFileEntity WHERE token=:token")
    suspend fun getTrackingFile(token: String): AvashoTrackingFileEntity?

    @Query(
        """
            SELECT * FROM AvashoProcessedFileEntity 
            WHERE fileName LIKE '%' || :searchText || '%' COLLATE NOCASE
        """
    )
    suspend fun searchAvashoArchiveItem(searchText: String): List<AvashoProcessedFileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessed(file: AvashoProcessedFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadingSpeech(avashoUploadingFileEntity: AvashoUploadingFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingSpeech(avashoTrackingFileEntity: AvashoTrackingFileEntity)

    @Query("UPDATE AvashoProcessedFileEntity SET filePath=:filePath WHERE id=:id")
    suspend fun updateFilePath(id: Int, filePath: String)

    @Query("UPDATE AvashoProcessedFileEntity SET isDownloading=:isDownloading WHERE id=:id")
    suspend fun updateDownloadStatus(id: Int, isDownloading: Boolean)

    @Query("UPDATE AvashoProcessedFileEntity SET fileName=:title WHERE id=:id")
    suspend fun updateTitle(title: String, id: Int)

    @Query(
        """
            UPDATE AvashoTrackingFileEntity 
            SET lastFailureSystemTime=:lastFailureSystemTime,
                lastFailureBootTime=:lastFailureBootTime
        """
    )
    suspend fun updateTrackingFileLastFailure(
        lastFailureSystemTime: Long?,
        lastFailureBootTime: Long?
    )

    @Query("DELETE FROM AvashoUploadingFileEntity WHERE id =:id")
    suspend fun deleteUploadingFile(id: String)

    @Query("DELETE FROM AvashoTrackingFileEntity WHERE token =:token")
    suspend fun deleteTrackingFile(token: String)

    @Query("DELETE FROM AvashoProcessedFileEntity WHERE id = :id")
    suspend fun deleteProcessedFile(id: Int)
}