package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.features.home.data.entity.VersionEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeLog(list: List<VersionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleaseNote(list: List<ReleaseNoteEntity>)

    @Query("DELETE FROM ReleaseNoteEntity")
    suspend fun deleteReleaseNote()

    @Query("SELECT * FROM VersionEntity ORDER BY versionNumber ASC")
    fun getChangeLog(): Flow<List<VersionDto>>
}