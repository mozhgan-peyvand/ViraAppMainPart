package ai.ivira.app.features.hamahang.ui

import ai.ivira.app.features.hamahang.ui.archive.HamahangArchiveListScreenRoute
import ai.ivira.app.features.hamahang.ui.detail.HamahangDetailScreenRoute
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangNewAudioScreenRoute
import ai.ivira.app.features.hamahang.ui.onboarding.HamahangOnboardingRoute
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.hamahangNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(
        route = HamahangScreenRoutes.HamahangNewAudioScreen.route,
        arguments = listOf(
            navArgument(name = "filePath") {
                type = NavType.StringType
                nullable = true
            })
    ) {
        HamahangNewAudioScreenRoute(navController)
    }

    navigateWithSlideAnimation(route = HamahangScreenRoutes.HamahangArchiveListScreen.route) {
        HamahangArchiveListScreenRoute(navController)
    }

    navigateWithSlideAnimation(
        route = HamahangScreenRoutes.HamahangDetailScreen.route,
        arguments = listOf(
            navArgument(name = "id") { type = NavType.IntType }
        )
    ) {
        HamahangDetailScreenRoute(navController)
    }

    navigateWithSlideAnimation(route = HamahangScreenRoutes.HamahangOnboardingScreen.route) {
        HamahangOnboardingRoute(navController = navController)
    }
}