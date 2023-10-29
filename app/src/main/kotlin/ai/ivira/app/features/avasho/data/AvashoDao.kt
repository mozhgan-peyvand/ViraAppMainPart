package ai.ivira.app.features.avasho.data

import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface AvashoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeechToDataBase(file: AvashoProcessedFileEntity)
}