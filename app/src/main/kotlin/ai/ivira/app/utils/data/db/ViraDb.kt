package ai.ivira.app.utils.data.db

import ai.ivira.app.features.ava_negar.data.AvanegarDao
import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AvanegarProcessedFileEntity::class,
        AvanegarTrackingFileEntity::class,
        AvanegarUploadingFileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ViraDb : RoomDatabase() {
    abstract fun avanegarDao(): AvanegarDao
}