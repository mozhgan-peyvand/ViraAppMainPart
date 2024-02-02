package ai.ivira.app.utils.ui.navigation

import ai.ivira.app.features.ava_negar.avanegarNavGraph
import ai.ivira.app.features.avasho.ui.avashoNavGraph
import ai.ivira.app.features.home.ui.homeNavGraph
import ai.ivira.app.features.splash.SplashScreenRoutes
import ai.ivira.app.features.splash.splashNavGraph
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

const val ANIMATION_NAVIGATION_DURATION_SLIDE = 300
const val ANIMATION_NAVIGATION_DURATION_FADE = 600

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SplashScreenRoutes.SplashScreen.route
    ) {
        splashNavGraph(navController)
        homeNavGraph(navController)
        avanegarNavGraph(navController)
        avashoNavGraph(navController)
    }
}

fun NavGraphBuilder.navigateWithSlideAnimation(
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