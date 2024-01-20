package ai.ivira.app

import ai.ivira.app.features.ava_negar.data.AvanegarTracker
import ai.ivira.app.features.avasho.data.AvashoTracker
import ai.ivira.app.utils.common.notification.FirebaseTopic.Vira
import ai.ivira.app.utils.common.notification.ViraFirebaseMessagingService.Companion.TAG
import ai.ivira.app.utils.ui.initializers.SentryInitializer
import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

const val IsSubScribeToVersion = "subScribeViraUpdateVersion"

@HiltAndroidApp
class ViraApp : Application(), ImageLoaderFactory {
    // this injection is here so that the tracking starts (it starts in init of this class)
    @Inject
    lateinit var avanegarTracker: AvanegarTracker

    @Inject
    lateinit var avashoTracker: AvashoTracker

    @Inject
    lateinit var sentryInitializer: SentryInitializer

    @Inject
    lateinit var sharePerf: SharedPreferences
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        sentryInitializer.init()

        getFirebaseToken()
        ensureViraTopicSubscribed()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .memoryCache {
                MemoryCache.Builder(applicationContext)
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(500 * 1024 * 1024)
                    .build()
            }
            .build()
    }

    private fun ensureViraTopicSubscribed() {
        val isSubscribe = sharePerf.getBoolean(IsSubScribeToVersion, false)
        if (!isSubscribe) {
            Firebase.messaging.subscribeToTopic(Vira)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sharePerf.edit {
                            this.putBoolean(IsSubScribeToVersion, true)
                        }
                        Timber.tag(TAG).v(task.isSuccessful.toString())
                    }
                }
        } else {
            Timber.tag(TAG).v("This topic has already been added")
        }
    }

    private fun getFirebaseToken() {
        Firebase.messaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Timber.tag(TAG).v(token)
                    return@OnCompleteListener
                }
            }
        )
    }
}