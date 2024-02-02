package ai.ivira.app.features.home.ui

sealed class HomeScreenRoutes(val route: String) {
    data object HomeMainOnboardingScreen : HomeScreenRoutes("home_main_onboarding_screen")
    data object HomeOnboardingScreen : HomeScreenRoutes("home_onboarding_screen")
    data object Home : HomeScreenRoutes("home_screen")
    data object AboutUs : HomeScreenRoutes("about_us")
}