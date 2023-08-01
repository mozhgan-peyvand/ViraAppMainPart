package ir.part.app.intelligentassistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.part.app.intelligentassistant.data.entity.AvanegarArchiveUnionEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvanegarDao {
    @Query("SELECT * FROM AvanegarProcessedFileEntity WHERE id=:id")
    fun getProcessedFileDetail(id: Int): Flow<AvanegarProcessedFileEntity?>

    @Query("SELECT * FROM AvanegarTrackingFileEntity WHERE token=:token")
    suspend fun getUnprocessedFileDetail(token: String): AvanegarTrackingFileEntity?

    @Query("SELECT * FROM AvanegarTrackingFileEntity")
    suspend fun getAllUnprocessedFiles(): List<AvanegarTrackingFileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnprocessedFile(file: AvanegarTrackingFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedFile(file: AvanegarProcessedFileEntity)

    @Query("DELETE FROM AvanegarTrackingFileEntity WHERE token = :token")
    suspend fun deleteUnprocessedFile(token: String)

    @Query(
        """
        SELECT * FROM (
            SELECT 0 AS id, title, '' AS text, createdAt, filePath, token, 0 AS isSeen
            FROM AvanegarTrackingFileEntity
            ORDER BY createdAt DESC
        )
        UNION ALL
        SELECT * FROM (
            SELECT id, title, text, createdAt, filePath, '' AS token, isSeen
            FROM AvanegarProcessedFileEntity
            ORDER BY createdAt DESC
        )
    """
    )
    fun getArchiveFiles(): Flow<List<AvanegarArchiveUnionEntity>>

    @Query("UPDATE AvanegarProcessedFileEntity SET isSeen=1 WHERE id=:id")
    suspend fun markFileAsSeen(id: Int)

    @Query("SELECT * FROM AvanegarProcessedFileEntity WHERE title LIKE '%' || :searchText || '%'")
    fun getSearch(searchText: String): Flow<List<AvanegarProcessedFileEntity>>

}