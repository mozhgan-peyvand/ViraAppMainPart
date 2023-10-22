package ai.ivira.app.utils.ui.analytics

import ai.ivira.app.BuildConfig
import ai.ivira.app.utils.ui.analytics.events.EventParams
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SearchEvent
import ai.ivira.app.utils.ui.analytics.events.SelectItemEvent
import ai.ivira.app.utils.ui.analytics.events.ShareEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

typealias FirebaseEvent = FirebaseAnalytics.Event
typealias FirebaseParam = FirebaseAnalytics.Param

@Singleton
class FirebaseEventHandler @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : EventHandler {
    private fun sendEvent(eventName: String, vararg params: Pair<String, String>) {
        // TODO: improve condition (should not rely on BuildConfig)
        // if (!BuildConfig.DEBUG) {
        firebaseAnalytics.logEvent(eventName) {
            params.filter { (key, value) ->
                key.isNotBlank() && value.isNotEmpty()
            }.forEach { (key, value) ->
                param(key, value)
            }
        }
        // }

        if (BuildConfig.DEBUG) {
            Timber.tag("FirebaseAnalyticsEventHandler")
                .d(
                    JSONObject().apply {
                        put("event", eventName)
                        put(
                            "params",
                            JSONObject().apply params@{
                                params.forEach { (key, value) ->
                                    put(key, value)
                                }
                            }
                        )
                    }.toString(2)
                )
        }
    }

    override fun screenViewEvent(event: ScreenViewEvent) {
        sendEvent(
            FirebaseEvent.SCREEN_VIEW,
            FirebaseParam.SCREEN_NAME to event.screenName,
            FirebaseParam.SCREEN_CLASS to event.screenClass,
            *event.params
        )
    }

    override fun onboardingEvent(event: OnboardingEvent) {
        sendEvent(
            event.eventName,
            EventParams.ORIGIN to event.origin.origin
        )
    }

    override fun specialEvent(event: SpecialEvent) {
        sendEvent(
            event.eventName,
            *event.params
        )
    }

    override fun selectItem(event: SelectItemEvent) {
        val params = buildList<Pair<String, String>> {
            event.contentType?.let { add(FirebaseParam.CONTENT_TYPE to event.contentType) }
            addAll(event.params)
        }
        sendEvent(
            FirebaseEvent.SELECT_ITEM,
            FirebaseParam.ITEM_NAME to event.itemName,
            *params.toTypedArray()
        )
    }

    override fun share(event: ShareEvent) {
        sendEvent(
            FirebaseEvent.SHARE,
            FirebaseParam.METHOD to event.method,
            FirebaseParam.ITEM_ID to event.itemId,
            FirebaseParam.CONTENT_TYPE to event.contentType
        )
    }

    override fun search(event: SearchEvent) {
        sendEvent(
            FirebaseEvent.SEARCH,
            FirebaseParam.SEARCH_TERM to event.searchTerm
        )
    }
}