package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhArchiveUnionEntity
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedFileEntity
import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImazhDao {
    @Query("SELECT * FROM ImazhHistoryEntity ORDER BY createdAt DESC LIMIT 5")
    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPromptToHistory(item: ImazhHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedFile(value: ImazhProcessedFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingFile(value: ImazhTrackingFileEntity)

    @Query("SELECT * FROM ImazhProcessedFileEntity ORDER BY id DESC")
    fun getAllProcessedFiles(): Flow<List<ImazhProcessedFileEntity>>

    @Query(
        """
        SELECT * FROM (
            SELECT 0 AS id, token, 'tracking' as archiveType,
                '' AS imagePath, '' AS filePath, keywords, prompt, negativePrompt, style, 
                   insertSystemTime,  processEstimation, insertBootTime, lastFailureSystemTime, lastFailureBootTime
            FROM ImazhTrackingFileEntity
        )
        UNION
        SELECT * FROM (
            SELECT id, '' AS token, 'processed' as archiveType,
                imagePath, filePath, keywords, prompt, negativePrompt, style,
                0 AS insertSystemTime, 0 AS insertBootTime, 0 AS lastFailureSystemTime, 0 AS lastFailureBootTime,
                0 AS processEstimation
            FROM ImazhProcessedFileEntity
        )
    """
    )
    fun getArchiveFiles(): Flow<List<ImazhArchiveUnionEntity>>

    @Query("SELECT * FROM ImazhTrackingFileEntity")
    fun getTrackingFiles(): Flow<List<ImazhTrackingFileEntity>>

    @Query("SELECT * FROM ImazhTrackingFileEntity WHERE token=:token")
    suspend fun getTrackingFile(token: String): ImazhTrackingFileEntity?

    @Query("SELECT * FROM ImazhProcessedFileEntity WHERE id=:id")
    fun getPhotoInfo(id: Int): Flow<ImazhProcessedFileEntity?>

    @Query("DELETE FROM ImazhProcessedFileEntity WHERE id=:id ")
    suspend fun deleteProcessedFile(id: Int)

    @Query("DELETE FROM ImazhTrackingFileEntity WHERE token =:token")
    suspend fun deleteTrackingFile(token: String)

    @Query("UPDATE ImazhProcessedFileEntity SET filePath=:filePath WHERE id=:id")
    suspend fun updateFilePath(id: Int, filePath: String)
}