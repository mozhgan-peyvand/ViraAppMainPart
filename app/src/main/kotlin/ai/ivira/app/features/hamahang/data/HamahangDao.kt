package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangCheckingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangUploadingFileEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HamahangDao {
    @Query("SELECT * FROM HamahangCheckingFileEntity ORDER BY createdAt DESC")
    fun getCheckingFiles(): Flow<List<HamahangCheckingFileEntity>>

    @Query("SELECT * FROM HamahangTrackingFileEntity ORDER BY insertSystemTime DESC")
    fun getTrackingFiles(): Flow<List<HamahangTrackingFileEntity>>

    @Query("SELECT * FROM HamahangUploadingFileEntity ORDER BY createdAt DESC")
    fun getUploadingFiles(): Flow<List<HamahangUploadingFileEntity>>

    @Query("SELECT * FROM HamahangProcessedFileEntity ORDER BY createdAt DESC")
    fun getProcessedFiles(): Flow<List<HamahangProcessedFileEntity>>

    @Query("SELECT * FROM HamahangProcessedFileEntity WHERE id=:id")
    fun getProcessedFile(id: Int): Flow<HamahangProcessedFileEntity>

    @Query("SELECT * FROM HamahangTrackingFileEntity WHERE token=:token")
    suspend fun getTrackingFile(token: String): HamahangTrackingFileEntity?

    @Query("SELECT * FROM HamahangCheckingFileEntity WHERE id=:id")
    suspend fun getCheckingFile(id: String): HamahangCheckingFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessedFile(value: HamahangProcessedFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadingFile(value: HamahangUploadingFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackingFile(value: HamahangTrackingFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckingFile(value: HamahangCheckingFileEntity)

    @Query("DELETE FROM HamahangProcessedFileEntity WHERE id =:id")
    suspend fun deleteProcessedFile(id: Int)

    @Query("DELETE FROM HamahangUploadingFileEntity WHERE id =:id")
    suspend fun deleteUploadingFile(id: String)

    @Query("DELETE FROM HamahangTrackingFileEntity WHERE token =:token")
    suspend fun deleteTrackingFile(token: String)

    @Query("DELETE FROM HamahangCheckingFileEntity WHERE id =:id")
    suspend fun deleteCheckingFile(id: String)

    @Query("UPDATE HamahangProcessedFileEntity SET isSeen=:isSeen WHERE id=:id")
    suspend fun markFileAsSeen(id: Int, isSeen: Boolean)

    @Query("UPDATE HamahangProcessedFileEntity SET title=:title WHERE id=:id")
    suspend fun updateTitle(title: String, id: Int)

    @Query("UPDATE HamahangCheckingFileEntity SET isProper=:isProper WHERE id=:id")
    suspend fun updateIsProper(id: String, isProper: Boolean)

    @Query(
        """
            UPDATE HamahangTrackingFileEntity 
            SET lastFailureSystemTime=:lastFailureSystemTime,
                lastFailureBootTime=:lastFailureBootTime
        """
    )
    suspend fun updateTrackingFileLastFailure(
        lastFailureSystemTime: Long?,
        lastFailureBootTime: Long?
    )
}