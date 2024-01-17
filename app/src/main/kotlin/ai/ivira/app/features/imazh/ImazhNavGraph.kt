package ai.ivira.app.features.imazh

import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveListScreenRoute
import ai.ivira.app.features.imazh.ui.details.ImazhDetailsScreenRoute
import ai.ivira.app.features.imazh.ui.newImageDescriptor.ImazhNewImageDescriptorScreenRoute
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhArchiveListScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhDetailsScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhNewImageDescriptorScreen
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.imazhNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = ImazhNewImageDescriptorScreen.route) {
        ImazhNewImageDescriptorScreenRoute(navController)
    }
    navigateWithSlideAnimation(route = ImazhArchiveListScreen.route) {
        ImazhArchiveListScreenRoute(navController)
    }

    navigateWithSlideAnimation(
        route = ImazhDetailsScreen.route,
        arguments = listOf(
            navArgument(name = "id") { type = NavType.IntType }
        )
    ) {
        ImazhDetailsScreenRoute(navController = navController)
    }
}