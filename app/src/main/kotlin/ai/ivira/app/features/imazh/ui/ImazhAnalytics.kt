package ai.ivira.app.features.imazh.ui

import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent

object ImazhAnalytics {
    // region screenView
    val screenViewArchiveList: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Archive", "ImazhArchiveListScreen")

    val screenViewDetails: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Detail", "ImazhDetailsScreen")

    val screenViewNewImageDescriptor: ScreenViewEvent
        get() = ScreenViewEvent("Imazh ImageDescriptor", "ImazhNewImageDescriptorScreen")

    val screenViewOnboarding: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Onboarding", "ImazhOnboardingScreen")

    // endregion screenView
}