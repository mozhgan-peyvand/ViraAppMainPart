package ai.ivira.app.features.home.ui

sealed class HomeScreenRoutes(val route: String) {
    data object HomeOnboardingScreen : HomeScreenRoutes("home_onboarding_screen")
    data object Home : HomeScreenRoutes("home_screen?isFirstRun={isFirstRun}") {
        fun createRoute(isFirstRun: Boolean = false): String {
            return "home_screen?isFirstRun=$isFirstRun"
        }
    }

    data object AboutUs : HomeScreenRoutes("about_us")
}