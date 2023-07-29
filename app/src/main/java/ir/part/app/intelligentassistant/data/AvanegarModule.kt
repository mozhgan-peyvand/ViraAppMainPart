package ir.part.app.intelligentassistant.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object AvanegarModule {
    @Provides
    fun provideMessengerService(retrofit: Retrofit): AvanegarService {
        return retrofit.create(AvanegarService::class.java)
    }
}