package ir.part.app.intelligentassistant.utils.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.AvaNegarArchiveListScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.details.AvaNegarArchiveDetailScreen
import ir.part.app.intelligentassistant.features.home.ui.HomeScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding.AvaNegarOnboardingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.search.AvaNegarSearchScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Home.route
    ) {
        composable(route = ScreenRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = ScreenRoutes.AvaNegarOnboarding.route) {
            AvaNegarOnboardingScreen(navController = navController)
        }
        composable(route = ScreenRoutes.AvaNegarArchiveList.route) {
            AvaNegarArchiveListScreen(navHostController = navController)
        }
        composable(
            route = ScreenRoutes.AvaNegarArchiveDetail.route.plus(
                "/{id}"
            ), arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            AvaNegarArchiveDetailScreen(
                navController = navController,
                itemId = backStackEntry.arguments?.getInt("id")
            )
        }
        composable(route = ScreenRoutes.AvaNegarVoiceRecording.route) {
            AvaNegarVoiceRecordingScreen()
        }
        composable(route = ScreenRoutes.AvaNegarSearch.route) {
            AvaNegarSearchScreen(navHostController = navController)
        }
    }
}