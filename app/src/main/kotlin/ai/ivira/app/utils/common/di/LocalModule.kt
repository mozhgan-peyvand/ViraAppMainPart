package ai.ivira.app.utils.common.di

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.utils.data.db.Migration
import ai.ivira.app.utils.data.db.ViraDb
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("vira", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideViraDb(@ApplicationContext context: Context): ViraDb {
        return Room
            .databaseBuilder(
                context,
                ViraDb::class.java,
                "vira.db"
            )
            .addMigrations(Migration.migration1_2())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)
}