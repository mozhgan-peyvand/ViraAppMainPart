package ai.ivira.app.features.imazh.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ImazhModule {
    @Singleton
    @Provides
    fun provideImazhDao(db: ViraDb): ImazhDao {
        return db.imazhDao()
    }

    @Provides
    fun provideImazhService(retrofit: Retrofit): ImazhService {
        return retrofit.create(ImazhService::class.java)
    }
}