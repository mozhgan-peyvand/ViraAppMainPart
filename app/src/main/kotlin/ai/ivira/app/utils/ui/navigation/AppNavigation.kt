package ai.ivira.app.utils.ui.navigation

import ai.ivira.app.features.ava_negar.ui.archive.AvaNegarArchiveListScreenRoute
import ai.ivira.app.features.ava_negar.ui.details.AvaNegarArchiveDetailScreenRoute
import ai.ivira.app.features.ava_negar.ui.onboarding.AvaNegarOnboardingScreenRoute
import ai.ivira.app.features.ava_negar.ui.record.AvaNegarVoiceRecordingScreenRoute
import ai.ivira.app.features.ava_negar.ui.search.AvaNegarSearchScreenRoute
import ai.ivira.app.features.home.ui.about_us.AboutUsScreenRoute
import ai.ivira.app.features.home.ui.home.HomeScreenRoute
import ai.ivira.app.features.home.ui.onboarding.HomeMainOnboardingScreenRoute
import ai.ivira.app.features.home.ui.onboarding.HomeOnboardingScreenRoute
import ai.ivira.app.features.splash.SplashScreenRoute
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
        avashoNavGraph(navController)
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
            SplashScreenRoute(navController = navController)
        }
        navigateWithSlideAnimationAndFadeInEnter(
            route = ScreenRoutes.HomeMainOnboardingScreen.route
        ) {
            HomeMainOnboardingScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.HomeOnboardingScreen.route) {
            HomeOnboardingScreenRoute(navController = navController)
        }
        navigateWithSlideAnimationAndFadeInEnter(route = ScreenRoutes.Home.route) {
            HomeScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AboutUs.route) {
            AboutUsScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarOnboarding.route) {
            AvaNegarOnboardingScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarArchiveList.route) {
            AvaNegarArchiveListScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(
            route = ScreenRoutes.AvaNegarArchiveDetail.route,
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
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarVoiceRecording.route) {
            AvaNegarVoiceRecordingScreenRoute(navController = navController)
        }
        navigateWithSlideAnimation(route = ScreenRoutes.AvaNegarSearch.route) {
            AvaNegarSearchScreenRoute(navController = navController)
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