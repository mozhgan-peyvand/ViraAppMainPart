package ai.ivira.app.utils.common.di

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.hamahang.data.HamahangDao
import ai.ivira.app.features.hamahang.data.HamahangFakeData
import ai.ivira.app.utils.common.di.qualifier.ConfigSharedPref
import ai.ivira.app.utils.common.di.qualifier.EncryptedSharedPref
import ai.ivira.app.utils.data.db.Migration
import ai.ivira.app.utils.data.db.ViraDb
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey.Builder
import androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM
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

    @Provides
    @Singleton
    @EncryptedSharedPref
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = Builder(context)
            .setKeyScheme(AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "e-vira",
            masterKey,
            AES256_SIV,
            PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    @ConfigSharedPref
    fun provideConfigSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = Builder(context)
            .setKeyScheme(AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "vira-config",
            masterKey,
            AES256_SIV,
            PrefValueEncryptionScheme.AES256_GCM
        )
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
            .addMigrations(Migration.migration2_3())
            .addMigrations(Migration.migration3_4())
            .addMigrations(Migration.migration4_5())
            .addMigrations(Migration.migration5_6())
            .addMigrations(Migration.migration6_7())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)
}