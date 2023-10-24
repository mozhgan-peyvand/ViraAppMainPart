package ai.ivira.app.utils.ui.analytics

import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SearchEvent
import ai.ivira.app.utils.ui.analytics.events.SelectItemEvent
import ai.ivira.app.utils.ui.analytics.events.ShareEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

interface EventHandler {
    fun screenViewEvent(event: ScreenViewEvent)

    fun onboardingEvent(event: OnboardingEvent)

    fun selectItem(event: SelectItemEvent)

    fun specialEvent(event: SpecialEvent)

    fun share(event: ShareEvent)

    fun search(event: SearchEvent)
}