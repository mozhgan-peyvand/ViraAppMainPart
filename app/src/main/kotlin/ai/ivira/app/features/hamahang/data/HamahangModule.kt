package ai.ivira.app.features.hamahang.data

import ai.ivira.app.utils.data.db.ViraDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object HamahangModule {
    @Provides
    fun provideHamahangService(retrofit: Retrofit): HamahangService {
        return retrofit.create(HamahangService::class.java)
    }

    @Singleton
    @Provides
    fun provideHamahangDao(db: ViraDb): HamahangDao {
        return db.hamahangDao()
    }
}