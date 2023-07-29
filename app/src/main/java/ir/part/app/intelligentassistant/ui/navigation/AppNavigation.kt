package ir.part.app.intelligentassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ir.part.app.intelligentassistant.ui.screen.AvaNegarArchiveScreen
import ir.part.app.intelligentassistant.ui.screen.AvaNegarOnBoardingScreen
import ir.part.app.intelligentassistant.ui.screen.AvaNegarProcessedArchiveDetailScreen
import ir.part.app.intelligentassistant.ui.screen.AvaNegarSearchScreen
import ir.part.app.intelligentassistant.ui.screen.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.ui.screen.ForceUpdateScreen
import ir.part.app.intelligentassistant.ui.screen.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreensRouter.HomeScreen.router
    ) {
        composable(route = ScreensRouter.HomeScreen.router) {
            HomeScreen()
        }
        composable(route = ScreensRouter.AvaNegarOnBoardingScreen.router) {
            AvaNegarOnBoardingScreen()
        }
        composable(route = ScreensRouter.AvaNegarArchiveScreen.router) {
            AvaNegarArchiveScreen()
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