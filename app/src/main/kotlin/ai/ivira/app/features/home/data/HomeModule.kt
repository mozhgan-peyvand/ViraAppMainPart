package ai.ivira.app.features.home.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object HomeModule {
    @Singleton
    @Provides
    fun provideVersionDao(db: ViraDb): VersionDao {
        return db.versionDao()
    }
}