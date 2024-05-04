package ai.ivira.app.utils.data.db

import ai.ivira.app.utils.data.getIntColumnOrNull
import ai.ivira.app.utils.data.getLongColumn
import ai.ivira.app.utils.data.getLongColumnOrNull
import ai.ivira.app.utils.data.getStringColumn
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration {
    fun migration1_2(): Migration {
        return object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `VersionEntity` (`versionNumber` INTEGER NOT NULL, `name` TEXT NOT NULL, `isForce` INTEGER NOT NULL, `versionName` TEXT NOT NULL, PRIMARY KEY(`versionNumber`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ReleaseNoteEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `versionNumber` INTEGER NOT NULL, `type` INTEGER NOT NULL, `title` TEXT NOT NULL)")
            }
        }
    }

    fun migration2_3(): Migration {
        return object : Migration(2, 3) {
            private fun migrateTrackingData(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `AvanegarTrackingFileEntityNew` (`token` TEXT NOT NULL, `processEstimation` INTEGER, `filePath` TEXT NOT NULL, `title` TEXT NOT NULL, `insertSystemTime` INTEGER NOT NULL, `insertBootTime` INTEGER NOT NULL, `lastFailureSystemTime` INTEGER, `lastFailureBootTime` INTEGER, PRIMARY KEY(`token`))")

                db.query("SELECT * FROM `AvanegarTrackingFileEntity`").use { cursor ->
                    while (cursor.moveToNext()) {
                        val token = cursor.getStringColumn("token")
                        val processEstimation = cursor.getIntColumnOrNull("processEstimation")
                        val filePath = cursor.getStringColumn("filePath")
                        val title = cursor.getStringColumn("title")
                        val createdAt = cursor.getLongColumn("createdAt")
                        val bootElapsedTime = cursor.getLongColumn("bootElapsedTime")
                        val lastFailedRequest = cursor.getLongColumnOrNull("lastFailedRequest")
                        val lastTrackedBootElapsed = cursor.getLongColumnOrNull("lastTrackedBootElapsed")

                        db.insert(
                            "AvanegarTrackingFileEntityNew",
                            SQLiteDatabase.CONFLICT_REPLACE,
                            ContentValues().apply {
                                put("token", token)
                                put("filePath", filePath)
                                put("title", title)
                                put("insertSystemTime", createdAt)
                                put("insertBootTime", bootElapsedTime)
                                put("processEstimation", processEstimation)
                                put("lastFailureSystemTime", lastFailedRequest)
                                put("lastFailureBootTime", lastTrackedBootElapsed)
                            }
                        )
                    }
                }

                db.execSQL("DROP TABLE `AvanegarTrackingFileEntity`")
                db.execSQL("ALTER TABLE `AvanegarTrackingFileEntityNew` RENAME TO `AvanegarTrackingFileEntity`")
            }

            override fun migrate(db: SupportSQLiteDatabase) {
                migrateTrackingData(db)

                db.execSQL("CREATE TABLE IF NOT EXISTS `AvashoProcessedFileEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fileUrl` TEXT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isDownloading` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `AvashoTrackingFileEntity` (`token` TEXT NOT NULL, `title` TEXT NOT NULL, `text` TEXT NOT NULL, `processEstimation` INTEGER, `insertSystemTime` INTEGER NOT NULL, `insertBootTime` INTEGER NOT NULL, `lastFailureSystemTime` INTEGER, `lastFailureBootTime` INTEGER, PRIMARY KEY(`token`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `AvashoUploadingFileEntity` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `text` TEXT NOT NULL, `speaker` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }
    }

    fun migration3_4(): Migration {
        return object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `TileConfigEntity` (`name` TEXT NOT NULL, `message` TEXT NOT NULL, `status` INTEGER NOT NULL, PRIMARY KEY(`name`))")
                db.execSQL("ALTER TABLE AvashoProcessedFileEntity ADD COLUMN `isSeen` INTEGER DEFAULT 1 not null")
            }
        }
    }

    fun migration4_5(): Migration {
        return object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `ImazhProcessedFileEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `imagePath` TEXT NOT NULL, `filePath` TEXT NOT NULL, `keywords` TEXT NOT NULL, `englishKeywords` TEXT NOT NULL, `prompt` TEXT NOT NULL, `englishPrompt` TEXT NOT NULL, `negativePrompt` TEXT NOT NULL, `englishNegativePrompt` TEXT NOT NULL, `style` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ImazhHistoryEntity` (`prompt` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`prompt`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ImazhTrackingFileEntity` (`token` TEXT NOT NULL, `keywords` TEXT NOT NULL, `englishKeywords` TEXT NOT NULL, `prompt` TEXT NOT NULL, `englishPrompt` TEXT NOT NULL, `negativePrompt` TEXT NOT NULL, `englishNegativePrompt` TEXT NOT NULL, `style` TEXT NOT NULL, `processEstimation` INTEGER, `insertSystemTime` INTEGER NOT NULL, `insertBootTime` INTEGER NOT NULL, `lastFailureSystemTime` INTEGER, `lastFailureBootTime` INTEGER, PRIMARY KEY(`token`))")
            }
        }
    }

    fun migration5_6(): Migration {
        return object : Migration(5, 6) {
            private fun migrateImazhProcessedFileEntity(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `ImazhProcessedFileEntityNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `imagePath` TEXT NOT NULL, `filePath` TEXT NOT NULL, `keywords` TEXT NOT NULL, `englishKeywords` TEXT NOT NULL, `prompt` TEXT NOT NULL, `englishPrompt` TEXT NOT NULL, `style` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `nsfw` INTEGER NOT NULL)")
                db.execSQL("INSERT INTO 'ImazhProcessedFileEntityNew' (id, imagePath, filePath, keywords, englishKeywords, prompt, englishPrompt, style, createdAt, nsfw) SELECT id, imagePath, filePath, keywords, englishKeywords, prompt, englishPrompt, style, createdAt, 0 AS nsfw FROM `ImazhProcessedFileEntity`")
                db.execSQL("DROP TABLE `ImazhProcessedFileEntity`")
                db.execSQL("ALTER TABLE `ImazhProcessedFileEntityNew` RENAME TO `ImazhProcessedFileEntity`")
            }

            private fun migrateImazhTrackingFileEntity(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `ImazhTrackingFileEntityNew` (`token` TEXT NOT NULL, `keywords` TEXT NOT NULL, `englishKeywords` TEXT NOT NULL, `prompt` TEXT NOT NULL, `englishPrompt` TEXT NOT NULL, `style` TEXT NOT NULL, `processEstimation` INTEGER, `insertSystemTime` INTEGER NOT NULL, `insertBootTime` INTEGER NOT NULL, `lastFailureSystemTime` INTEGER, `lastFailureBootTime` INTEGER, PRIMARY KEY(`token`))")
                db.execSQL("INSERT INTO 'ImazhTrackingFileEntityNew' (token, keywords, englishKeywords, prompt, englishPrompt, style, processEstimation, insertSystemTime, insertBootTime, lastFailureSystemTime, lastFailureBootTime) SELECT token, keywords, englishKeywords, prompt, englishPrompt, style, processEstimation, insertSystemTime, insertBootTime, lastFailureSystemTime, lastFailureBootTime FROM 'ImazhTrackingFileEntity'")
                db.execSQL("DROP TABLE `ImazhTrackingFileEntity`")
                db.execSQL("ALTER TABLE `ImazhTrackingFileEntityNew` RENAME TO `ImazhTrackingFileEntity`")
            }

            override fun migrate(db: SupportSQLiteDatabase) {
                migrateImazhProcessedFileEntity(db)
                migrateImazhTrackingFileEntity(db)
            }
        }
    }

    fun migration6_7(): Migration {
        return object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ImazhTrackingFileEntity ADD COLUMN `negativePrompt` Text DEFAULT '' NOT NULL")
                db.execSQL("ALTER TABLE ImazhTrackingFileEntity ADD COLUMN `englishNegativePrompt` Text DEFAULT '' NOT NULL")

                db.execSQL("ALTER TABLE ImazhProcessedFileEntity ADD COLUMN `negativePrompt` Text DEFAULT '' NOT NULL")
                db.execSQL("ALTER TABLE ImazhProcessedFileEntity ADD COLUMN `englishNegativePrompt` Text DEFAULT '' NOT NULL")

                db.execSQL("DROP TABLE IF EXISTS `TileConfigEntity`")
                db.execSQL("DROP TABLE IF EXISTS `VersionEntity`")
                db.execSQL("DROP TABLE IF EXISTS `ReleaseNoteEntity`")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ConfigTileEntity` (`name` TEXT NOT NULL, `message` TEXT NOT NULL, `status` INTEGER NOT NULL, PRIMARY KEY(`name`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ConfigVersionEntity` (`versionNumber` INTEGER NOT NULL, `isForce` INTEGER NOT NULL, `versionName` TEXT NOT NULL, PRIMARY KEY(`versionNumber`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ConfigVersionReleaseNoteEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `versionNumber` INTEGER NOT NULL, `type` INTEGER NOT NULL, `title` TEXT NOT NULL)")
            }
        }
    }
}