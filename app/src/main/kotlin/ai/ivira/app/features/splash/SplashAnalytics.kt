package ai.ivira.app.features.splash

import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent

object SplashAnalytics {
    // region screenView
    val screenViewSplash: ScreenViewEvent
        get() = ScreenViewEvent("Splash", "SplashScreen")
    // endregion screenView
}