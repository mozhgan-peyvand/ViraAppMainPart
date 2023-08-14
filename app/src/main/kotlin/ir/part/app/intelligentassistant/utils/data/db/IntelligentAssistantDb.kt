package ir.part.app.intelligentassistant.utils.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.part.app.intelligentassistant.data.AvanegarDao
import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarUploadingFileEntity

@Database(
    entities = [
        AvanegarProcessedFileEntity::class,
        AvanegarTrackingFileEntity::class,
        AvanegarUploadingFileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IntelligentAssistantDb : RoomDatabase() {
    abstract fun avanegarDao(): AvanegarDao
}