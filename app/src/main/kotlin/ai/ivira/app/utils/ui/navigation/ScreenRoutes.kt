package ai.ivira.app.utils.ui.navigation

sealed class ScreenRoutes(val route: String) {
    data object ImazhNewImageDescriptorScreen : ScreenRoutes("imazhNewImageDescriptor_screen")
    data object ImazhArchiveListScreen : ScreenRoutes("imazhArchive_screen")
    data object ImazhDetailsScreen : ScreenRoutes("imazhDetails_Screen?id={id}") {
        fun createRoute(id: Int): String {
            return "imazhDetails_Screen?id=$id"
        }
    }
}