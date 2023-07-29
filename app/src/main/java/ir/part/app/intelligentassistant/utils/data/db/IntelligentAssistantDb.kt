package ir.part.app.intelligentassistant.utils.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TempEntity::class // TODO: remove!!
    ],
    version = 1,
    exportSchema = false
)
abstract class IntelligentAssistantDb : RoomDatabase()