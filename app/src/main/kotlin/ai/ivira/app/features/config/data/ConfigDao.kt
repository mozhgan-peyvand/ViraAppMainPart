package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.model.ConfigHamahangSpeakerEntity
import ai.ivira.app.features.config.data.model.ConfigTileEntity
import ai.ivira.app.features.config.data.model.ConfigVersionEntity
import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    // region tiles
    @Query("SELECT * FROM ConfigTileEntity")
    fun getTiles(): Flow<List<ConfigTileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTiles(configs: List<ConfigTileEntity>)

    @Query("DELETE FROM ConfigTileEntity")
    suspend fun deleteTiles()
    // endregion tiles

    // region versions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersions(list: List<ConfigVersionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleaseNotes(list: List<ConfigVersionReleaseNoteEntity>)

    @Query("DELETE FROM ConfigVersionEntity")
    suspend fun deleteVersions()

    @Query("DELETE FROM ConfigVersionReleaseNoteEntity")
    suspend fun deleteReleaseNotes()
    // endregion versions

    // region hamahang
    @Query("SELECT * FROM ConfigHamahangSpeakerEntity")
    fun getHamahangSpeakers(): Flow<List<ConfigHamahangSpeakerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHamahangSpeakers(speakers: List<ConfigHamahangSpeakerEntity>)

    @Query("DELETE FROM ConfigHamahangSpeakerEntity")
    suspend fun deleteHamahangSpeakers()
    // endregion hamahang
}