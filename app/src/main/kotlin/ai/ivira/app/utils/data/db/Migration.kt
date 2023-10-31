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

                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `AvashoProcessedFileEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `checksum` TEXT NOT NULL, `filePath` TEXT NOT NULL, `fileName` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL )"
                )
            }
        }
    }
}