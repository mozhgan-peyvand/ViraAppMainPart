package ai.ivira.app.features.home.ui

import ai.ivira.app.features.home.ui.about_us.AboutUsScreenRoute
import ai.ivira.app.features.home.ui.home.HomeScreenRoute
import ai.ivira.app.features.home.ui.onboarding.HomeOnboardingScreenRoute
import ai.ivira.app.features.home.ui.terms.TermsOfServicesScreenRoute
import ai.ivira.app.utils.ui.navigation.ANIMATION_NAVIGATION_DURATION_FADE
import ai.ivira.app.utils.ui.navigation.ANIMATION_NAVIGATION_DURATION_SLIDE
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = HomeScreenRoutes.HomeOnboardingScreen.route) {
        HomeOnboardingScreenRoute(navController = navController)
    }
    navigateWithSlideAnimationAndFadeInEnter(
        route = HomeScreenRoutes.Home.route,
        arguments = listOf(
            navArgument("isFirstRun") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) {
        HomeScreenRoute(navController = navController)
    }
    navigateWithSlideAnimation(route = HomeScreenRoutes.AboutUs.route) {
        AboutUsScreenRoute(navController = navController)
    }

    navigateWithSlideAnimation(route = HomeScreenRoutes.TermsOfServiceScreen.route) {
        TermsOfServicesScreenRoute(navController = navController)
    }
}

private fun NavGraphBuilder.navigateWithSlideAnimationAndFadeInEnter(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
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