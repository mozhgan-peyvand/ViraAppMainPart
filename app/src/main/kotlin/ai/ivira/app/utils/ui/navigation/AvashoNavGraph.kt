package ai.ivira.app.utils.ui.navigation

import ai.ivira.app.features.avasho.ui.archive.AvashoArchiveListScreen
import ai.ivira.app.features.avasho.ui.file_creation.AvashoFileCreationScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoArchiveScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoFileCreationScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.avashoNavGraph(navController: NavHostController) {
    composable(route = AvaShoArchiveScreen.route) {
        AvashoArchiveListScreen(navController)
    }

    navigateWithSlideAnimation(route = AvaShoFileCreationScreen.route) {
        AvashoFileCreationScreen(navController)
    }
}