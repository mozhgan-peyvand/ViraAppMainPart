package ai.ivira.app.features.home.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.features.login.ui.LoginScreenRoutes
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeOnboardingScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(HomeAnalytics.screenViewOnboardingStart)
    }

    HomeOnboardingScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun HomeOnboardingScreen(
    navController: NavHostController,
    viewModel: HomeOnboardingViewModel
) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            eventHandler.onboardingEvent(HomeAnalytics.onboardingEnd)
            viewModel.onBoardingShown()
            navController.popBackStack()
            navController.navigate(
                route = LoginScreenRoutes.LoginMobileScreen.createRoute(fromSplash = false)
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ViraImage(
                drawable = R.drawable.img_main_onboarding,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            ) {
                ViraImage(
                    drawable = R.drawable.ic_vira_title,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(id = R.string.lbl_intelligence_services),
                    style = MaterialTheme.typography.body1,
                    color = Color_Text_2,
                    textAlign = TextAlign.Center
                )
            }
        }

        Card(
            border = BorderStroke(1.dp, Color_Card_Stroke),
            backgroundColor = Color_Card,
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.padding(bottom = 58.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = R.string.lbl_drag_to_start),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Text_2
                )

                Spacer(modifier = Modifier.size(12.dp))

                SwipeForDismiss(modifier = Modifier.padding(vertical = 8.dp)) {
                    viewModel.navigateToHomeScreen()
                }

                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun SwipeForDismiss(
    modifier: Modifier,
    onDismiss: () -> Unit
) {
    val dismissState = rememberDismissState(initialValue = DismissValue.Default)

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = {},
        dismissContent = {
            Card(
                backgroundColor = Color_Primary_300,
                shape = RoundedCornerShape(32.dp)
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_arrow_right,
                    contentDescription = null,
                    tint = Color_White,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 12.dp)
                )
            }
        },
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { _ ->
            FractionalThreshold(0.50f)
        }
    )

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDismiss.invoke()
    }
}

@ViraDarkPreview
@Composable
private fun HomeOnboardingScreenPreview() {
    ViraPreview {
        HomeOnboardingScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}