package ir.part.app.intelligentassistant.features.home.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LOTTIE_ANIMATION_DURATION = 1000
private const val APP_NAME_ANIMATION_DURATION = 1000
private const val SWIPEABLE_CARD_ANIMATION_DURATION = 1500
private const val APP_DESCRIPTION_DURATION = 3000
private const val DELAY_TO_SHOW_APP_DESCRIPTION = 1800
const val DELAY_TO_SHOW_LOGO_AND_APP_NAME = 200L
const val DELAY_TO_SHOW_SWIPEABLE = 2900L

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_vira_splash)
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

    val colorList = listOf(Color_BG, Color_Text_2)
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

    var isLottieAndAppNameVisible by remember {
        mutableStateOf(false)
    }

    var isSwipeToStartVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isSwipeToStartVisible, viewModel.shouldNavigate.value, viewModel.hasOnboardingShown.value) {
        if (isSwipeToStartVisible) {

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
    }

    LaunchedEffect(isLottieAndAppNameVisible, isSwipeToStartVisible) {
        launch(Dispatchers.IO) {
            delay(DELAY_TO_SHOW_LOGO_AND_APP_NAME)
            isLottieAndAppNameVisible = true
        }

        launch(Dispatchers.IO) {
            if (isLottieAndAppNameVisible) {
                delay(DELAY_TO_SHOW_SWIPEABLE)
                isSwipeToStartVisible = true
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
            .paint(
                painter = painterResource(id = R.drawable.bg_pattern),
                contentScale = ContentScale.Crop
            )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = isLottieAndAppNameVisible,
                enter = slideInVertically(
                    // Enters by sliding down from offset -fullHeight to 0.
                    initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(
                        durationMillis = LOTTIE_ANIMATION_DURATION, easing = FastOutLinearInEasing
                    )
                ),
            ) {

                LottieAnimation(
                    modifier = Modifier.size(81.dp),
                    composition = composition,
                )
            }

            AnimatedVisibility(
                visible = isLottieAndAppNameVisible, enter = slideInVertically(
                    // Enters by sliding down from offset -fullHeight to 0.
                    initialOffsetY = { fullHeight -> -fullHeight }, animationSpec = tween(
                        durationMillis = APP_NAME_ANIMATION_DURATION, easing = FastOutLinearInEasing
                    )
                )
            ) {

                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h3,
                    fontSize = 40.sp,
                    color = Color_White,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.lbl_intelligence_services),
                color = Color_Text_2,
                style = MaterialTheme.typography.body1.copy(brush = brush)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //todo should remove it
            AnimatedVisibility(
                visible = isSwipeToStartVisible,
                enter = slideInVertically(
                    // Enters by sliding down from offset -fullHeight to 0.
                    initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(
                        durationMillis = SWIPEABLE_CARD_ANIMATION_DURATION,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {

                Card(
                    border = BorderStroke(1.dp, Color_Card_Stroke),
                    backgroundColor = Color_Card,
                    shape = RoundedCornerShape(32.dp),
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    ) {

                        Spacer(modifier = Modifier.size(12.dp))

                        Text(
                            text = stringResource(id = R.string.lbl_drag_to_start),
                            style = MaterialTheme.typography.subtitle1,
                            color = Color_Text_2
                        )

                        Spacer(modifier = Modifier.size(12.dp))

                        SwipeToDismiss(modifier = Modifier.padding(vertical = 8.dp)) {
                            viewModel.navigateToMainOnboarding()
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.size(58.dp))
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun SwipeToDismiss(
    modifier: Modifier, onDismiss: () -> Unit
) {
    val dismissState = rememberDismissState(initialValue = DismissValue.Default)

    SwipeToDismiss(modifier = modifier, state = dismissState, background = {}, dismissContent = {
        Card(
            backgroundColor = Color_Primary_300, shape = RoundedCornerShape(32.dp)
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 25.dp, vertical = 12.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Color_White
            )
        }
    }, directions = setOf(DismissDirection.EndToStart), dismissThresholds = { _ ->
        FractionalThreshold(0.50f)
    })

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDismiss.invoke()
    }
}


@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SplashScreen(rememberNavController())
        }
    }
}
