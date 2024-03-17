package ai.ivira.app.features.hamahang.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object HamahangModule {
    @Provides
    fun provideHamahangService(retrofit: Retrofit): HamahangService {
        return retrofit.create(HamahangService::class.java)
    }
}