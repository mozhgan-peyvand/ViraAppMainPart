package ai.ivira.app.features.imazh.ui

sealed class ImazhScreenRoutes(val route: String) {
    data object ImazhNewImageDescriptorScreen : ImazhScreenRoutes("imazhNewImageDescriptor_screen?id={id}") {
        fun createRoute(id: Int = -1): String {
            return "imazhNewImageDescriptor_screen?id=$id"
        }
    }

    data object ImazhArchiveListScreen : ImazhScreenRoutes("imazhArchive_screen")
    data object ImazhOnboardingScreen : ImazhScreenRoutes("imazhOnboarding_screen")
    data object ImazhDetailsScreen : ImazhScreenRoutes("imazhDetails_Screen?id={id}") {
        fun createRoute(id: Int): String {
            return "imazhDetails_Screen?id=$id"
        }
    }
}