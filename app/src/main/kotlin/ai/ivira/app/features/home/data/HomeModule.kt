package ai.ivira.app.features.home.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object HomeModule {
    @Provides
    fun provideVersionService(retrofit: Retrofit): VersionService {
        return retrofit.create(VersionService::class.java)
    }

    @Singleton
    @Provides
    fun provideVersionDao(db: ViraDb): VersionDao {
        return db.versionDao()
    }
}