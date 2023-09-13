package ir.part.app.intelligentassistant.utils.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.AvaNegarArchiveListScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.details.AvaNegarArchiveDetailScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding.AvaNegarOnboardingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreen
import ir.part.app.intelligentassistant.features.ava_negar.ui.search.AvaNegarSearchScreen
import ir.part.app.intelligentassistant.features.home.about_us.AboutUsScreen
import ir.part.app.intelligentassistant.features.home.onboarding.HomeMainOnboardingScreen
import ir.part.app.intelligentassistant.features.home.onboarding.HomeOnboardingScreen
import ir.part.app.intelligentassistant.features.home.splash.SplashScreen
import ir.part.app.intelligentassistant.features.home.ui.HomeScreen

private const val ANIMATION_NAVIGATION_DURATION = 300

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SplashScreen.route
    ) {
        navigateWithSlideAnimation(route = ScreenRoutes.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.HomeMainOnboardingScreen.route) {
            HomeMainOnboardingScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.HomeOnboardingScreen.route) {
            HomeOnboardingScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AboutUs.route) {
            AboutUsScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarOnboarding.route) {
            AvaNegarOnboardingScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarArchiveList.route) {
            AvaNegarArchiveListScreen(navHostController = navController)
        }
        navigateWithSlideAnimation(
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
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarVoiceRecording.route) {
            AvaNegarVoiceRecordingScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarSearch.route) {
            AvaNegarSearchScreen(navHostController = navController)
        }
    }
}

private fun NavGraphBuilder.navigateWithSlideAnimation(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION)
            )
        }
    ) {
        content(it)
    }
}