package ai.ivira.app.features.splash

sealed class SplashScreenRoutes(val route: String) {
    data object SplashScreen : SplashScreenRoutes("splash_screen")
}