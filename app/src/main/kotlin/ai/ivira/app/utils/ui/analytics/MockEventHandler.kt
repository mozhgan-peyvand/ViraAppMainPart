package ai.ivira.app.utils.ui.analytics

import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SearchEvent
import ai.ivira.app.utils.ui.analytics.events.SelectItemEvent
import ai.ivira.app.utils.ui.analytics.events.ShareEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

class MockEventHandler : EventHandler {
    override fun screenViewEvent(event: ScreenViewEvent) = Unit

    override fun onboardingEvent(event: OnboardingEvent) = Unit

    override fun selectItem(event: SelectItemEvent) = Unit

    override fun specialEvent(event: SpecialEvent) = Unit

    override fun share(event: ShareEvent) = Unit

    override fun search(event: SearchEvent) = Unit
}