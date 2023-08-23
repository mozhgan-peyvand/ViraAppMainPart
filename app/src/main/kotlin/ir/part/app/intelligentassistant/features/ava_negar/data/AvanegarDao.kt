package ir.part.app.intelligentassistant.features.ava_negar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarArchiveUnionEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarUploadingFileEntity
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
            SELECT 0 AS id,'' AS uploadingId, title, '' AS text, createdAt, filePath, token, 0 AS isSeen
            FROM AvanegarTrackingFileEntity
            ORDER BY createdAt DESC
        )
        UNION ALL
        SELECT * FROM (
            SELECT id,'' AS uploadingId, title, text, createdAt, filePath, '' AS token, isSeen
            FROM AvanegarProcessedFileEntity
            ORDER BY createdAt DESC
        )
        UNION ALL
        SELECT * FROM (
            SELECT 0 AS id, id AS uploadingId, title, '' AS text, createdAt, filePath,  '' AS token,  0 AS isSeen
            FROM AvanegarUploadingFileEntity
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