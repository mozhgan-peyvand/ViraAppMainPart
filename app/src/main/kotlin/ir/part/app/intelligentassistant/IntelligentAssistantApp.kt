package ir.part.app.intelligentassistant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ir.part.app.intelligentassistant.features.ava_negar.data.AvanegarTracker
import javax.inject.Inject

@HiltAndroidApp
class IntelligentAssistantApp : Application() {
    // this injection is here so that the tracking starts (it starts in init of this class)
    @Inject
    lateinit var avanegarTracker: AvanegarTracker
}