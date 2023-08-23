package ir.part.app.intelligentassistant.utils.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.AvaNegarArchiveScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.details.AvaNegarProcessedArchiveDetailScreen
import ir.part.app.intelligentassistant.features.home.ui.HomeScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding.AvaNegarOnBoardingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.search.AvaNegarSearchScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreensRouter.HomeScreen.router
    ) {
        composable(route = ScreensRouter.HomeScreen.router) {
            HomeScreen(navController = navController)
        }
        composable(route = ScreensRouter.AvaNegarOnBoardingScreen.router) {
            AvaNegarOnBoardingScreen(navController = navController)
        }
        composable(route = ScreensRouter.AvaNegarArchiveScreen.router) {
            AvaNegarArchiveScreen(navHostController = navController)
        }
        composable(
            route = ScreensRouter.AvaNegarProcessedArchiveDetailScreen.router.plus(
                "/{id}"
            ), arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            AvaNegarProcessedArchiveDetailScreen(
                navController = navController,
                itemId = backStackEntry.arguments?.getInt("id")
            )
        }
        composable(route = ScreensRouter.AvaNegarVoiceRecordingScreen.router) {
            AvaNegarVoiceRecordingScreen()
        }
        composable(route = ScreensRouter.AvaNegarSearchScreen.router) {
            AvaNegarSearchScreen(navHostController = navController)
        }
    }
}