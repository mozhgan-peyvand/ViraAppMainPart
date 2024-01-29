package ai.ivira.app.features.config.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM TileConfigEntity")
    fun getTileConfigs(): Flow<List<TileConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTileConfigs(configs: List<TileConfigEntity>)
}