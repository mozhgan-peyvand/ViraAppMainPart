package ai.ivira.app.features.splash

import ai.ivira.app.utils.ui.navigation.ANIMATION_NAVIGATION_DURATION_FADE
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.splashNavGraph(navController: NavHostController) {
    composable(
        route = SplashScreenRoutes.SplashScreen.route,
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
}