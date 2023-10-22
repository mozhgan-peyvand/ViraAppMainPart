package ai.ivira.app.utils.common.di

import ai.ivira.app.utils.ui.analytics.EventHandler
import ai.ivira.app.utils.ui.analytics.FirebaseEventHandler
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FirebaseModule {
    @Binds
    fun bindFirebaseEventHandler(eventHandler: FirebaseEventHandler): EventHandler

    companion object {
        @Singleton // TODO: is this right?
        @Provides
        fun provideFirebaseAnalytics(): FirebaseAnalytics {
            return Firebase.analytics
        }
    }
}