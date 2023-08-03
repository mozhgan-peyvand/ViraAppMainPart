package ir.part.app.intelligentassistant.utils.di

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.part.app.intelligentassistant.data.DataStoreRepository
import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.utils.data.db.IntelligentAssistantDb
import saman.zamani.persiandate.PersianDate
import javax.inject.Singleton

//TODO remove these fake data
val processedList = listOf(
    AvanegarProcessedFileEntity(
        id = 6135,
        title = "title1",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6136,
        title = "title2",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6137,
        title = "title3",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6138,
        title = "title4",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6139,
        title = "title5",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6140,
        title = "title6",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),
    AvanegarProcessedFileEntity(
        id = 6141,
        title = "title7",
        text = "vitae",
        createdAt = PersianDate().time,
        filePath = "libero",
        isSeen = false
    ),

    )

val trackingList = listOf(
    AvanegarTrackingFileEntity(
        token = "1",
        filePath = "11",
        title = "title1",
        createdAt = PersianDate().time,
    ),
    AvanegarTrackingFileEntity(
        token = "2",
        filePath = "22",
        title = "title2",
        createdAt = PersianDate().time,
    ),
    AvanegarTrackingFileEntity(
        token = "3",
        filePath = "33",
        title = "title3",
        createdAt = PersianDate().time,
    ),
    AvanegarTrackingFileEntity(
        token = "4",
        filePath = "44",
        title = "title4",
        createdAt = PersianDate().time,
    ),
)

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("IntelligentAssistant", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideIntelligentAssistantDb(@ApplicationContext context: Context): IntelligentAssistantDb {
        return Room
            .databaseBuilder(
                context,
                IntelligentAssistantDb::class.java,
                "assistant.db"
            )

            //TODO remove this callback
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    processedList.forEach {
                        val values = ContentValues()
                        values.put("id", it.id)
                        values.put("title", it.title)
                        values.put("text", it.text)
                        values.put("createdAt", it.createdAt)
                        values.put("filePath", it.filePath)
                        values.put("isSeen", it.isSeen)

                        db.insert(
                            "AvanegarProcessedFileEntity",
                            SQLiteDatabase.CONFLICT_REPLACE,
                            values
                        )
                    }
                    trackingList.forEach {
                        val values = ContentValues()
                        values.put("token", it.token)
                        values.put("filePath", it.filePath)
                        values.put("title", it.title)
                        values.put("createdAt", it.createdAt)

                        db.insert(
                            "AvanegarTrackingFileEntity",
                            SQLiteDatabase.CONFLICT_REPLACE,
                            values
                        )
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)
}