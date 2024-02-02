package ai.ivira.app.features.avasho.ui

sealed class AvashoScreenRoutes(val route: String) {
    data object AvaShoOnboardingScreen : AvashoScreenRoutes("avashoOnboarding_screen")
    data object AvaShoArchiveScreen : AvashoScreenRoutes("avashoArchive_screen")
    data object AvaShoFileCreationScreen : AvashoScreenRoutes("avashoFileCreation_screen")
    data object AvashoSearchScreen : AvashoScreenRoutes("avashoSearch_screen")
}