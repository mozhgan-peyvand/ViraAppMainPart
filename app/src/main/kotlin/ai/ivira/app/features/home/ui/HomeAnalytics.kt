package ai.ivira.app.features.home.ui

import ai.ivira.app.features.home.ui.home.HomeItemBottomSheetType
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent.Type
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SelectItemEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object HomeAnalytics {
    private const val COMING_SOON_EVENT = "comingSoon"
    private const val INTRODUCE_TO_FRIENDS = "introduce_to_friends"

    // region screenView
    val screenViewHome: ScreenViewEvent
        get() = ScreenViewEvent("Home", "HomeScreen")

    val screenViewAboutUs: ScreenViewEvent
        get() = ScreenViewEvent("About us", "AboutUsScreen")

    val screenViewOnboardingStart: ScreenViewEvent
        get() = ScreenViewEvent("Home Onboarding Start", "HomeMainOnboardingScreen")

    val screenViewOnboardingSlides: ScreenViewEvent
        get() = ScreenViewEvent("Home Onboarding Slides", "HomeOnboardingScreen")
    // endregion screenView

    // region onboarding
    val onboardingStart: OnboardingEvent
        get() = OnboardingEvent(Type.Beginning, HomeOnboarding)

    val onboardingEnd: OnboardingEvent
        get() = OnboardingEvent(Type.End, HomeOnboarding)
    // endregion onboarding

    // region selectItem
    fun selectComingSoonItem(type: HomeItemBottomSheetType): SelectItemEvent {
        return SelectItemEvent(
            itemName = COMING_SOON_EVENT,
            contentType = type.value
        )
    }
    // endregion selectItem

    // region specialEvents
    val introduceToFriends: SpecialEvent
        get() = SpecialEvent(INTRODUCE_TO_FRIENDS)
    // endregion specialEvents

    object HomeOnboarding : OnboardingEvent.Origin {
        override val origin: String = "Home"
    }
}