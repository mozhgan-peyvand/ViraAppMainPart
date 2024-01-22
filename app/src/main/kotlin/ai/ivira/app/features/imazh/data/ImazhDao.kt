package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity
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
    suspend fun insertPhotoInfo(imazhProcessedEntity: ImazhProcessedEntity)

    @Query("SELECT * FROM ImazhProcessedEntity ORDER BY id DESC")
    fun getAllProcessedFiles(): Flow<List<ImazhProcessedEntity>>

    @Query("SELECT * FROM ImazhProcessedEntity WHERE id=:id")
    fun getPhotoInfo(id: Int): Flow<ImazhProcessedEntity?>

    @Query("DELETE FROM ImazhProcessedEntity WHERE id=:id ")
    suspend fun deletePhotoInfo(id: Int)

    @Query("UPDATE ImazhProcessedEntity SET filePath=:filePath WHERE id=:id")
    suspend fun updateFilePath(id: Int, filePath: String)
}