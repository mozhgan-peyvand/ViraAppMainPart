package ai.ivira.app.features.login.ui

import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object LoginAnalytics {
    private const val PREFIX = "login"

    // region screenView
    val screenViewLoginMobile: ScreenViewEvent
        get() = ScreenViewEvent("Login Mobile", "LoginMobileScreen")

    val screenViewLoginOtp: ScreenViewEvent
        get() = ScreenViewEvent("Login Otp", "LoginOtpScreen")
    // endregion

    // region specialEvents
    val login: SpecialEvent
        get() = SpecialEvent("${PREFIX}_login")

    val logout: SpecialEvent
        get() = SpecialEvent("${PREFIX}_logout")
    // endregion specialEvents
}