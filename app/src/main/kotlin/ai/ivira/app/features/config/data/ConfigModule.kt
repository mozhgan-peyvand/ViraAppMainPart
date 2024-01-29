package ai.ivira.app.features.config.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object ConfigModule {
    @Provides
    fun provideConfigService(retrofit: Retrofit): ConfigService {
        return retrofit.create(ConfigService::class.java)
    }

    @Provides
    fun provideConfigDao(db: ViraDb): ConfigDao {
        return db.tileConfigDao()
    }
}