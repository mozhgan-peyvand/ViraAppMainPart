package ir.part.app.intelligentassistant.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.part.app.intelligentassistant.utils.data.db.IntelligentAssistantDb
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
    fun provideAvanegarDao(db: IntelligentAssistantDb): AvanegarDao {
        return db.avanegarDao()
    }
}