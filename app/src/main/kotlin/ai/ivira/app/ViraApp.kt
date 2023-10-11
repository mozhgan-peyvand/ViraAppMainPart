package ai.ivira.app

import ai.ivira.app.features.ava_negar.data.AvanegarTracker
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ViraApp : Application() {
    // this injection is here so that the tracking starts (it starts in init of this class)
    @Inject
    lateinit var avanegarTracker: AvanegarTracker

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}