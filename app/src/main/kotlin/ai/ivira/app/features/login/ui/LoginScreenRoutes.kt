package ai.ivira.app.features.login.ui

sealed class LoginScreenRoutes(val route: String) {
    data object LoginMobileScreen : LoginScreenRoutes(route = "loginMobile_screen?fromSplash={fromSplash}") {
        fun createRoute(fromSplash: Boolean): String {
            return "loginMobile_screen?fromSplash=$fromSplash"
        }
    }

    data object LoginOtpScreen : LoginScreenRoutes(route = "loginOtp_screen?mobile={mobile}") {
        fun createRoute(mobile: String): String {
            return "loginOtp_screen?mobile=$mobile"
        }
    }
}