package ai.ivira.app.features.avasho.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AvashoModule {
    @Provides
    fun provideAvashoService(retrofit: Retrofit): AvashoService {
        return retrofit.create(AvashoService::class.java)
    }

    @Singleton
    @Provides
    fun provideAvashoDao(db: ViraDb): AvashoDao {
        return db.avashoDao()
    }
}