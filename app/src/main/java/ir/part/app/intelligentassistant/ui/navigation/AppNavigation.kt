package ir.part.app.intelligentassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ir.part.app.intelligentassistant.ui.screen.archive.AvaNegarArchiveScreen
import ir.part.app.intelligentassistant.ui.screen.archive.AvaNegarProcessedArchiveDetailScreen
import ir.part.app.intelligentassistant.ui.screen.home.HomeScreen
import ir.part.app.intelligentassistant.ui.screen.onBoarding.AvaNegarOnBoardingScreen
import ir.part.app.intelligentassistant.ui.screen.record.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.ui.screen.search.AvaNegarSearchScreen
import ir.part.app.intelligentassistant.ui.screen.update.ForceUpdateScreen

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
        composable(route = ScreensRouter.ForceUpdateScreen.router) {
            ForceUpdateScreen()
        }
        composable(route = ScreensRouter.AvaNegarProcessedArchiveDetailScreen.router) {
            AvaNegarProcessedArchiveDetailScreen()
        }
        composable(route = ScreensRouter.AvaNegarVoiceRecordingScreen.router) {
            AvaNegarVoiceRecordingScreen()
        }
        composable(route = ScreensRouter.AvaNegarSearchScreen.router) {
            AvaNegarSearchScreen()
        }
    }
}