package ai.ivira.app.features.splash

import ai.ivira.app.R
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Light_blue_50_2
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val APP_DESCRIPTION_DURATION = 1700
private const val DELAY_TO_SHOW_APP_DESCRIPTION = 1300
private const val APP_NAME_ANIMATION_DURATION = 500
private const val DELAY_TO_NAVIGATE = 500L

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
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

    var isAppNameVisible by rememberSaveable { mutableStateOf(false) }

    if (lottieProgress > 0.001f && !isAppNameVisible) {
        isAppNameVisible = true
    }

    val animateColorBlueGrayOrBlueLight by animateColorAsState(
        targetValue = Light_blue_50_2,
        animationSpec = tween(easing = EaseIn),
        label = ""
    )

    val animationSpec = TweenSpec<Float>(
        durationMillis = APP_DESCRIPTION_DURATION,
        delay = DELAY_TO_SHOW_APP_DESCRIPTION,
        easing = LinearEasing
    )
    val progress = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        progress.animateTo(-3f, animationSpec)
    }

    val colorList = listOf(Color_BG, Light_blue_50_2)
    val brush = remember(progress.value) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * progress.value
                val heightOffset = size.height * progress.value
                return LinearGradientShader(
                    colors = colorList,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Clamp
                )
            }
        }
    }

    LaunchedEffect(
        viewModel.shouldNavigate.value,
        viewModel.hasOnboardingShown.value
    ) {
        if (viewModel.shouldNavigate.value) {
            if (viewModel.hasOnboardingShown.value) {
                navController.popBackStack()
                navController.navigate(
                    route = ScreenRoutes.Home.route
                )
            } else {
                navController.popBackStack()
                navController.navigate(
                    route = ScreenRoutes.HomeMainOnboardingScreen.route
                )
            }
        }
    }

    LaunchedEffect(lottieProgress) {
        launch(IO) {
            if (lottieProgress == 1f) {
                delay(DELAY_TO_NAVIGATE)
                viewModel.navigateToMainOnboarding()
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
            progress = lottieProgress,
            modifier = Modifier
                .size(94.dp)
                .padding(bottom = 6.dp)
        )

        AnimatedVisibility(
            visible = isAppNameVisible,
            enter = fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(APP_NAME_ANIMATION_DURATION)
            )
        ) {
            ViraImage(
                drawable = R.drawable.ic_app_name,
                contentDescription = null
            )
        }

        Text(
            text = stringResource(id = R.string.lbl_assistant),
            style = MaterialTheme.typography.body1.copy(brush = brush),
            color = animateColorBlueGrayOrBlueLight
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SplashScreen(rememberNavController())
        }
    }
}