package ai.ivira.app.utils.ui.navigation

sealed class ScreenRoutes(val route: String) {
    data object ImazhNewImageDescriptorScreen : ScreenRoutes("imazhNewImageDescriptor_screen")
    data object ImazhArchiveListScreen : ScreenRoutes("imazhArchive_screen")
}