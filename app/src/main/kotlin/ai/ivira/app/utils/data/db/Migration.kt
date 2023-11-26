package ai.ivira.app.utils.data.db

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
}