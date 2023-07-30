package ir.part.app.intelligentassistant.utils.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.part.app.intelligentassistant.data.DataStoreRepository
import ir.part.app.intelligentassistant.utils.data.db.IntelligentAssistantDb
import javax.inject.Singleton

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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)
}