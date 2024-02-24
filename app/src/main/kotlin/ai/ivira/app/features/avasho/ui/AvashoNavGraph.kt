package ai.ivira.app.features.avasho.ui

import ai.ivira.app.features.avasho.ui.archive.AvashoArchiveListScreenRoute
import ai.ivira.app.features.avasho.ui.file_creation.AvashoFileCreationScreenRoute
import ai.ivira.app.features.avasho.ui.onboarding.AvashoOnboardingScreenRoute
import ai.ivira.app.features.avasho.ui.search.AvashoSearchScreenRoute
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.avashoNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = AvashoScreenRoutes.AvaShoOnboardingScreen.route) {
        AvashoOnboardingScreenRoute(navController)
    }

    navigateWithSlideAnimation(route = AvashoScreenRoutes.AvaShoArchiveScreen.route) {
        AvashoArchiveListScreenRoute(navController)
    }

    navigateWithSlideAnimation(route = AvashoScreenRoutes.AvaShoFileCreationScreen.route) {
        AvashoFileCreationScreenRoute(navController)
    }

    navigateWithSlideAnimation(route = AvashoScreenRoutes.AvashoSearchScreen.route) {
        AvashoSearchScreenRoute(navController)
    }
}