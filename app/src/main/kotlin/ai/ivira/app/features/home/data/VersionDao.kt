package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.VersionDto
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Transaction
    @Query("SELECT * FROM ConfigVersionEntity ORDER BY versionNumber ASC")
    fun getChangeLog(): Flow<List<VersionDto>>
}