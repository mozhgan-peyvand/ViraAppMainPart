package ai.ivira.app.features.home.ui

import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent.Type
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
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

    // endregion screenView

    // region onboarding
    val onboardingStart: OnboardingEvent
        get() = OnboardingEvent(Type.Beginning, HomeOnboarding)

    val onboardingEnd: OnboardingEvent
        get() = OnboardingEvent(Type.End, HomeOnboarding)
    // endregion onboarding

    // region specialEvents
    val openAvanegar: SpecialEvent
        get() = SpecialEvent("open_avanegar")
    val openAvasho: SpecialEvent
        get() = SpecialEvent("avasho_open")
    val introduceToFriends: SpecialEvent
        get() = SpecialEvent(INTRODUCE_TO_FRIENDS)
    val checkUpdate: SpecialEvent
        get() = SpecialEvent("check_update")
    val showUpdateLater: SpecialEvent
        get() = SpecialEvent("show_update_later")
    val updateApp: SpecialEvent
        get() = SpecialEvent("update_app")

    fun selectComingSoonItem(type: HomeItemBottomSheetType): SpecialEvent {
        return SpecialEvent("coming_soon_${type.value}")
    }
    // endregion specialEvents

    object HomeOnboarding : OnboardingEvent.Origin {
        override val origin: String = "Home"
    }
}