package ai.ivira.app.features.imazh

import ai.ivira.app.features.imazh.ui.newImageDescriptor.ImazhNewImageDescriptorScreenRoute
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhNewImageDescriptorScreen
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.imazhNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = ImazhNewImageDescriptorScreen.route) {
        ImazhNewImageDescriptorScreenRoute(navController)
    }
}