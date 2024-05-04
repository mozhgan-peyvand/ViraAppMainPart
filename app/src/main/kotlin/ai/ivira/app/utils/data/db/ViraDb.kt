package ai.ivira.app.utils.data.db

import ai.ivira.app.features.ava_negar.data.AvanegarDao
import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity
import ai.ivira.app.features.avasho.data.AvashoDao
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity
import ai.ivira.app.features.config.data.ConfigDao
import ai.ivira.app.features.config.data.model.ConfigTileEntity
import ai.ivira.app.features.config.data.model.ConfigVersionEntity
import ai.ivira.app.features.config.data.model.ConfigVersionReleaseNoteEntity
import ai.ivira.app.features.home.data.VersionDao
import ai.ivira.app.features.imazh.data.ImazhDao
import ai.ivira.app.features.imazh.data.ImazhListConvertor
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedFileEntity
import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(ImazhListConvertor::class)
@Database(
    entities = [
        // region: Avanegar
        AvanegarProcessedFileEntity::class,
        AvanegarTrackingFileEntity::class,
        AvanegarUploadingFileEntity::class,
        // endregion: Avanegar
        // region: Config
        ConfigVersionEntity::class,
        ConfigVersionReleaseNoteEntity::class,
        ConfigTileEntity::class,
        // endregion: Config
        // region: Avasho
        AvashoProcessedFileEntity::class,
        AvashoTrackingFileEntity::class,
        AvashoUploadingFileEntity::class,
        // endregion: Avasho
        // region: Imazh
        ImazhHistoryEntity::class,
        ImazhProcessedFileEntity::class,
        ImazhTrackingFileEntity::class
        // endregion: Imazh
    ],
    version = 7,
    exportSchema = false
)
abstract class ViraDb : RoomDatabase() {
    abstract fun avanegarDao(): AvanegarDao
    abstract fun versionDao(): VersionDao
    abstract fun avashoDao(): AvashoDao
    abstract fun tileConfigDao(): ConfigDao
    abstract fun imazhDao(): ImazhDao
}