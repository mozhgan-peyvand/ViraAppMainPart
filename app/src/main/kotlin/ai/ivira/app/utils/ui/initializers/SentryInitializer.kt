package ai.ivira.app.utils.ui.initializers

import ai.ivira.app.BuildConfig
import android.content.Context
import android.text.format.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SentryInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized: Boolean = false

    init {
        System.loadLibrary("vira")
    }

    fun init() {
        if (isInitialized) return

        SentryAndroid.init(context) { options ->
            options.dsn = dsn()
            options.isDebug = BuildConfig.DEBUG
            options.dist = BuildConfig.VERSION_NAME
            options.isEnableUserInteractionTracing = true
            options.isEnableUserInteractionBreadcrumbs = true
            options.isEnableAutoSessionTracking = true
            // https://docs.sentry.io/platforms/android/configuration/releases/#sessions
            options.sessionTrackingIntervalMillis = 45 * DateUtils.SECOND_IN_MILLIS
            // https://docs.sentry.io/platforms/android/configuration/app-not-respond/#attaching-thread-dump
            options.isAttachAnrThreadDump = true

            options.environment = let {
                if (BuildConfig.BUILD_TYPE.hashCode() == "release".hashCode()) {
                    BuildConfig.FLAVOR
                } else {
                    BuildConfig.FLAVOR_source
                }
            }
            options.release = "${BuildConfig.APPLICATION_ID}@${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}"
            options.beforeSend = SentryOptions.BeforeSendCallback { event, hint ->
                if (event.environment == null) return@BeforeSendCallback null

                event
            }
            options.beforeBreadcrumb = SentryOptions.BeforeBreadcrumbCallback { breadcrumb, hint ->
                if (breadcrumb.getData(DATA_TAG) in ignoreBreadCrumbTags) {
                    return@BeforeBreadcrumbCallback null
                }
                breadcrumb.getData(DATA_ACTION)?.let { action ->
                    if (action in ignoredActions) {
                        return@BeforeBreadcrumbCallback null
                    }
                }
                breadcrumb.removeData(DATA_NETWORK_TYPE)
                breadcrumb.removeData(DATA_SIGNAL_STRENGTH)

                breadcrumb
            }
        }
    }

    private external fun dsn(): String

    companion object {
        private const val DATA_TAG = "tag"
        private const val DATA_ACTION = "action"
        private const val DATA_NETWORK_TYPE = "network_type"
        private const val DATA_SIGNAL_STRENGTH = "signal_strength"

        private val ignoreBreadCrumbTags = listOf("LOTTIE")
        private val ignoredActions = listOf(
            "NETWORK_CAPABILITIES_CHANGED", "NETWORK_LOST", "NETWORK_AVAILABLE"
        )
    }
}