package ai.ivira.app.features.login.ui

import ai.ivira.app.features.login.ui.LoginScreenRoutes.LoginMobileScreen
import ai.ivira.app.features.login.ui.LoginScreenRoutes.LoginOtpScreen
import ai.ivira.app.features.login.ui.mobile.LoginMobileRoute
import ai.ivira.app.features.login.ui.otp.LoginOtpRoute
import ai.ivira.app.utils.ui.navigation.navigateWithSlideAnimation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigateWithSlideAnimation(route = LoginMobileScreen.route) {
        LoginMobileRoute(navController)
    }

    navigateWithSlideAnimation(route = LoginOtpScreen.route) {
        LoginOtpRoute(navController)
    }
}