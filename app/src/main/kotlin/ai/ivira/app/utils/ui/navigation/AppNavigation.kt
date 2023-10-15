package ai.ivira.app.utils.ui.navigation

import ai.ivira.app.features.ava_negar.ui.archive.AvaNegarArchiveListScreen
import ai.ivira.app.features.ava_negar.ui.details.AvaNegarArchiveDetailScreen
import ai.ivira.app.features.ava_negar.ui.onboarding.AvaNegarOnboardingScreen
import ai.ivira.app.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreen
import ai.ivira.app.features.ava_negar.ui.search.AvaNegarSearchScreen
import ai.ivira.app.features.home.ui.about_us.AboutUsScreen
import ai.ivira.app.features.home.ui.home.HomeScreen
import ai.ivira.app.features.home.ui.onboarding.HomeMainOnboardingScreen
import ai.ivira.app.features.home.ui.onboarding.HomeOnboardingScreen
import ai.ivira.app.features.splash.SplashScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

private const val ANIMATION_NAVIGATION_DURATION_SLIDE = 300
private const val ANIMATION_NAVIGATION_DURATION_FADE = 600

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SplashScreen.route
    ) {
        composable(
            route = ScreenRoutes.SplashScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_NAVIGATION_DURATION_FADE))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_NAVIGATION_DURATION_FADE))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_NAVIGATION_DURATION_FADE))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_NAVIGATION_DURATION_FADE))
            }
        ) {
            SplashScreen(navController = navController)
        }
        navigateWithSlideAnimationAndFadeInEnter(
            route = ScreenRoutes.HomeMainOnboardingScreen.route
        ) {
            HomeMainOnboardingScreen(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.HomeOnboardingScreen.route) {
            HomeOnboardingScreen(navController = navController)
        }
        navigateWithSlideAnimationAndFadeInEnter(route = ScreenRoutes.Home.route) {
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
            route = ScreenRoutes.AvaNegarArchiveDetail.route.plus("/{id}"),
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
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
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        }
    ) {
        content(it)
    }
}

private fun NavGraphBuilder.navigateWithSlideAnimationAndFadeInEnter(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_NAVIGATION_DURATION_FADE))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(ANIMATION_NAVIGATION_DURATION_SLIDE)
            )
        }
    ) {
        content(it)
    }
}