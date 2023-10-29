package ai.ivira.app.utils.data.db

import ai.ivira.app.features.ava_negar.data.AvanegarDao
import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.home.data.VersionDao
import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.VersionEntity
import ai.ivira.app.features.avasho.data.AvashoDao
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AvanegarProcessedFileEntity::class,
        AvanegarTrackingFileEntity::class,
        AvanegarUploadingFileEntity::class,
        VersionEntity::class,
        ReleaseNoteEntity::class,
        AvanegarUploadingFileEntity::class,
        AvashoProcessedFileEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ViraDb : RoomDatabase() {
    abstract fun avanegarDao(): AvanegarDao
    abstract fun versionDao(): VersionDao
    abstract fun avashoDao(): AvashoDao
}