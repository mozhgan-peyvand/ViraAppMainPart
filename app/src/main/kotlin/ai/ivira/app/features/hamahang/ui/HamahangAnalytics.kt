package ai.ivira.app.features.hamahang.ui

import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object HamahangAnalytics {
    private const val PREFIX = "hamahang"
    private const val ORIGIN_HAMAHANG = "Hamahang"

    // region screenView
    val screenViewArchiveList: ScreenViewEvent
        get() = ScreenViewEvent("Hamahang Archive", "HamahangArchiveListScreen")

    val screenViewDetails: ScreenViewEvent
        get() = ScreenViewEvent("Hamahang Detail", "HamahangDetailScreen")

    val screenViewNewAudio: ScreenViewEvent
        get() = ScreenViewEvent("Hamahang NewAudio", "HamahangNewAudioScreen")

    // region onboarding
    val onboardingStart: OnboardingEvent
        get() = OnboardingEvent(OnboardingEvent.Type.Beginning, HamahangOnboarding)

    val onboardingEnd: OnboardingEvent
        get() = OnboardingEvent(OnboardingEvent.Type.End, HamahangOnboarding)
    // endregion onboarding

    val screenViewOnboarding: ScreenViewEvent
        get() = ScreenViewEvent("Hamahang Onboarding", "HamahangOnboardingScreen")

    // endregion screenView

    // region specialEvents
    val voiceConverted: SpecialEvent
        get() = SpecialEvent("${PREFIX}_voice_converted")

    val shareVoice: SpecialEvent
        get() = SpecialEvent("${PREFIX}_share_voice")

    val downloadVoice: SpecialEvent
        get() = SpecialEvent("${PREFIX}_download_voice")
    // endregion specialEvents

    object HamahangOnboarding : OnboardingEvent.Origin {
        override val origin: String = ORIGIN_HAMAHANG
    }
}