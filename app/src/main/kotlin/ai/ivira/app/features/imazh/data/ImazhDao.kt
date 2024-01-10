package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImazhDao {
    @Query("SELECT * FROM ImazhHistoryEntity ORDER BY prompt DESC LIMIT 5")
    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessed(item: List<ImazhHistoryEntity>)
}