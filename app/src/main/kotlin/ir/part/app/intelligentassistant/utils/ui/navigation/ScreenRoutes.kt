package ir.part.app.intelligentassistant.utils.ui.navigation

sealed class ScreenRoutes(val route: String) {
    object SplashScreen : ScreenRoutes("splash_screen")
    object HomeMainOnboardingScreen : ScreenRoutes("home_main_onboarding_screen")
    object HomeOnboardingScreen : ScreenRoutes("home_onboarding_screen")
    object Home : ScreenRoutes("home_screen")
    object AvaNegarVoiceRecording :
        ScreenRoutes("avaNegarVoiceRecording_screen")

    object AvaNegarSearch : ScreenRoutes("avaNegarSearch_screen")
    object AvaNegarArchiveDetail :
        ScreenRoutes("avaNegarProcessedArchiveDetail_screen")

    object AvaNegarOnboarding :
        ScreenRoutes("avaNegarOnBoarding_screen")

    object AvaNegarArchiveList : ScreenRoutes("avaNegarArchive_screen")
}
