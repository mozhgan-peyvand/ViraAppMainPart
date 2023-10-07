package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AvanegarModule {
    @Provides
    fun provideMessengerService(retrofit: Retrofit): AvanegarService {
        return retrofit.create(AvanegarService::class.java)
    }

    @Singleton
    @Provides
    fun provideAvanegarDao(db: ViraDb): AvanegarDao {
        return db.avanegarDao()
    }
}