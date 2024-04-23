package ai.ivira.app.features.login.ui

import ai.ivira.app.features.home.ui.terms.TermsOfServicesScreenRoute
import ai.ivira.app.features.login.ui.LoginScreenRoutes.LoginMobileScreen
import ai.ivira.app.features.login.ui.LoginScreenRoutes.LoginOtpScreen
import ai.ivira.app.features.login.ui.mobile.LoginMobileRoute
import ai.ivira.app.features.login.ui.otp.LoginOtpRoute
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = LoginMobileScreen.route) {
        LoginMobileRoute(navController)
    }

    navigateWithSlideAnimation(
        route = LoginOtpScreen.route,
        arguments = listOf(
            navArgument("mobile") {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { backStackEntry ->
        val mobile = backStackEntry.arguments?.getString("mobile") ?: error("could not fine mobile")
        LoginOtpRoute(navController, mobile)
    }
}