package ir.part.app.intelligentassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.part.app.intelligentassistant.ui.screen.archive.AvaNegarArchiveScreen
import ir.part.app.intelligentassistant.ui.screen.details.AvaNegarProcessedArchiveDetailScreen
import ir.part.app.intelligentassistant.ui.screen.home.HomeScreen
import ir.part.app.intelligentassistant.ui.screen.onBoarding.AvaNegarOnBoardingScreen
import ir.part.app.intelligentassistant.ui.screen.record.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.ui.screen.search.AvaNegarSearchScreen

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
            AvaNegarOnBoardingScreen(navController)
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