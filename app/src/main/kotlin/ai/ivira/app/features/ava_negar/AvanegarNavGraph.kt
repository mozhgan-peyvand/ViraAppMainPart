package ai.ivira.app.features.ava_negar

import ai.ivira.app.features.ava_negar.ui.archive.AvaNegarArchiveListScreenRoute
import ai.ivira.app.features.ava_negar.ui.details.AvaNegarArchiveDetailScreenRoute
import ai.ivira.app.features.ava_negar.ui.onboarding.AvaNegarOnboardingScreenRoute
import ai.ivira.app.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreenRoute
import ai.ivira.app.features.ava_negar.ui.search.AvaNegarSearchScreenRoute
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.avanegarNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = AvanegarScreenRoutes.AvaNegarOnboarding.route) {
        AvaNegarOnboardingScreenRoute(navController = navController)
    }
    navigateWithSlideAnimation(route = AvanegarScreenRoutes.AvaNegarArchiveList.route) {
        AvaNegarArchiveListScreenRoute(navController = navController)
    }
    navigateWithSlideAnimation(
        route = AvanegarScreenRoutes.AvaNegarArchiveDetail.route,
        arguments = listOf(
            navArgument("id") {
                type = NavType.IntType
            },
            navArgument("title") {
                type = NavType.StringType
                defaultValue = ""
                nullable = false
            }
        )
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id") ?: error("could not fine id")
        val title = backStackEntry.arguments?.getString("title") ?: error("could not fine id")
        AvaNegarArchiveDetailScreenRoute(
            navController = navController,
            id = id,
            title = title
        )
    }
    navigateWithSlideAnimation(route = AvanegarScreenRoutes.AvaNegarVoiceRecording.route) {
        AvaNegarVoiceRecordingScreenRoute(navController = navController)
    }
    navigateWithSlideAnimation(route = AvanegarScreenRoutes.AvaNegarSearch.route) {
        AvaNegarSearchScreenRoute(navController = navController)
    }
}