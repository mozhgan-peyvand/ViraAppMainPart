package ai.ivira.app.features.splash

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeScreenRoutes
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_BG
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@Composable
fun SplashScreenRoute(navController: NavController) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(SplashAnalytics.screenViewSplash)
    }
    SplashScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_vira_splash)
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        restartOnPlay = true,
        clipSpec = null,
        speed = 1f,
        iterations = 1
    )

    LaunchedEffect(
        viewModel.shouldNavigate.value,
        viewModel.hasOnboardingShown.value
    ) {
        if (viewModel.shouldNavigate.value) {
            if (viewModel.hasOnboardingShown.value) {
                navController.popBackStack()
                navController.navigate(
                    route = HomeScreenRoutes.Home.createRoute()
                )
            } else {
                navController.popBackStack()
                navController.navigate(
                    route = HomeScreenRoutes.HomeMainOnboardingScreen.route
                )
            }
        }
    }

    LaunchedEffect(lottieProgress) {
        launch(IO) {
            if (lottieProgress == 1f) {
                viewModel.startNavigation()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) {
        LottieAnimation(
            composition = composition,
            progress = lottieProgress
        )
    }
}

@ViraDarkPreview
@Composable
private fun SplashScreenPreview() {
    ViraPreview {
        SplashScreenRoute(rememberNavController())
    }
}