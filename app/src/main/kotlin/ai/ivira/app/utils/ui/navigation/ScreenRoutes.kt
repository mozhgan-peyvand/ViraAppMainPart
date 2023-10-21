package ai.ivira.app.utils.ui.navigation

sealed class ScreenRoutes(val route: String) {
    data object SplashScreen : ScreenRoutes("splash_screen")
    data object HomeMainOnboardingScreen : ScreenRoutes("home_main_onboarding_screen")
    data object HomeOnboardingScreen : ScreenRoutes("home_onboarding_screen")
    data object Home : ScreenRoutes("home_screen")
    data object AboutUs : ScreenRoutes("about_us")
    data object AvaNegarVoiceRecording :
        ScreenRoutes("avaNegarVoiceRecording_screen")

    data object AvaNegarSearch : ScreenRoutes("avaNegarSearch_screen")
    data object AvaNegarArchiveDetail :
        ScreenRoutes("avaNegarProcessedArchiveDetail_screen?id={id}&title={title}") {
        fun createRoute(id: Int, title: String): String {
            return "avaNegarProcessedArchiveDetail_screen?id=$id&title=$title"
        }
    }

    data object AvaNegarOnboarding :
        ScreenRoutes("avaNegarOnBoarding_screen")

    data object AvaNegarArchiveList : ScreenRoutes("avaNegarArchive_screen")
    data object AvaShoArchiveScreen : ScreenRoutes("avashoArchive_screen")
    data object AvaShoFileCreationScreen : ScreenRoutes("avashoFileCreation_Screen")
}