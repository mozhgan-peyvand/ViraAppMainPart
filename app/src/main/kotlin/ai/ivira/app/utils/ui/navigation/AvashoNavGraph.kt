package ai.ivira.app.utils.ui.navigation

import ai.ivira.app.features.avasho.ui.archive.AvashoArchiveListScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoArchiveScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.avashoNavGraph(navController: NavHostController) {
    composable(route = AvaShoArchiveScreen.route) {
        AvashoArchiveListScreen(navController)
    }
}