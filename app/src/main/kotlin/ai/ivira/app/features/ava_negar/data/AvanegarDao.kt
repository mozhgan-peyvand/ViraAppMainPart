package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarArchiveUnionEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

const val TRACKING_ITEM = "tracking"
const val UPLOADING_ITEM = "uploading"
const val PROCESSED_ITEM = "processed"

@Dao
interface AvanegarDao {
    @Query("SELECT * FROM AvanegarProcessedFileEntity WHERE id=:id")
    fun getProcessedFileDetail(id: Int): Flow<AvanegarProcessedFileEntity?>

    @Query("SELECT * FROM AvanegarTrackingFileEntity")
    fun getTrackingFiles(): Flow<List<AvanegarTrackingFileEntity>>

    @Query("SELECT * FROM AvanegarTrackingFileEntity")
    suspend fun getTrackingFilesSync(): List<AvanegarTrackingFileEntity>

    @Query("SELECT * FROM AvanegarTrackingFileEntity WHERE token=:token")
    suspend fun getUnprocessedFileDetail(token: String): AvanegarTrackingFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnprocessedFile(file: AvanegarTrackingFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedFile(file: AvanegarProcessedFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadingFile(file: AvanegarUploadingFileEntity)

    @Query("DELETE FROM AvanegarTrackingFileEntity WHERE token = :token")
    suspend fun deleteUnprocessedFile(token: String)

    @Query("DELETE FROM AvanegarProcessedFileEntity WHERE id = :id")
    suspend fun deleteProcessedFile(id: Int?)

    @Query("DELETE FROM AvanegarUploadingFileEntity WHERE id =:id")
    suspend fun deleteUploadingFile(id: String)

    @Query("UPDATE AvanegarProcessedFileEntity SET title=:title WHERE id=:id")
    suspend fun updateTitle(title: String?, id: Int?)

    @Query("UPDATE AvanegarProcessedFileEntity SET text=:text WHERE id=:id")
    suspend fun editText(text: String, id: Int)

    @Query(
        """
        SELECT * FROM (
            SELECT 0 AS id,'' AS uploadingId, title, 0 AS fileDuration, '' AS text, createdAt, filePath, token, 0 AS isSeen, 'tracking' as archiveType,
            processEstimation, bootElapsedTime, lastFailedRequest, lastTrackedBootElapsed
            FROM AvanegarTrackingFileEntity
            ORDER BY createdAt DESC
        )
        UNION
        SELECT * FROM (
            SELECT id,'' AS uploadingId, title, 0 AS fileDuration, text, createdAt, filePath, '' AS token, isSeen, 'processed' as archiveType,
            0 AS processEstimation, 0 AS bootElapsedTime, 0 AS lastFailedRequest, 0 AS lastTrackedBootElapsed
            FROM AvanegarProcessedFileEntity
            ORDER BY createdAt DESC
        )
        UNION
        SELECT * FROM (
            SELECT 0 AS id, id AS uploadingId, title, fileDuration,'' AS text, createdAt, filePath,  '' AS token,  0 AS isSeen, 'uploading' as archiveType,
            0 AS processEstimation, 0 AS bootElapsedTime, 0 AS lastFailedRequest, 0 AS lastTrackedBootElapsed
            FROM AvanegarUploadingFileEntity
            ORDER BY ROWID ASC
        )
    """
    )
    fun getArchiveFiles(): Flow<List<AvanegarArchiveUnionEntity>>

    @Query("UPDATE AvanegarProcessedFileEntity SET isSeen=1 WHERE id=:id")
    suspend fun markFileAsSeen(id: Int)

    @Query(
        "SELECT * FROM AvanegarProcessedFileEntity WHERE title LIKE '%' || :searchText || '%' COLLATE NOCASE"
    )
    suspend fun getSearch(searchText: String): List<AvanegarProcessedFileEntity>

    @Query(
        "UPDATE AvanegarTrackingFileEntity SET lastFailedRequest=:lastFailedRequest, lastTrackedBootElapsed=:lastTrackedBootElapsed"
    )
    suspend fun updateLastTrackingFileFailure(
        lastFailedRequest: Long?,
        lastTrackedBootElapsed: Long?
    )
}