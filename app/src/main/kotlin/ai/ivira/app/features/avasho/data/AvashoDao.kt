package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoArchiveUnionEntity
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
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
            SELECT id, text, fileName, createdAt, fileUrl, filePath,  checksum, isDownloading, '' AS speaker, 'processed' as archiveType
            FROM AvashoProcessedFileEntity
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
}