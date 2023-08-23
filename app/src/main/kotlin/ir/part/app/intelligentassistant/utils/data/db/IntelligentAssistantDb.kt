package ir.part.app.intelligentassistant.utils.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarDao
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarUploadingFileEntity

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