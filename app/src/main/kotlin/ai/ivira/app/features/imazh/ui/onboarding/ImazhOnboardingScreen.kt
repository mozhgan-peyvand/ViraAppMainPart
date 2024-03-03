package ai.ivira.app.features.imazh.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.features.imazh.ui.ImazhAnalytics
import ai.ivira.app.features.imazh.ui.ImazhScreenRoutes
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Cyan_200
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun ImazhOnboardingScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(ImazhAnalytics.screenViewOnboarding)
    }

    ImazhOnboardingScreen(
        viewModel = hiltViewModel(),
        navController = navController
    )
}

@Composable
private fun ImazhOnboardingScreen(
    viewModel: ImazhOnboardingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            navController.navigate(ImazhScreenRoutes.ImazhArchiveListScreen.route) {
                popUpTo(ImazhScreenRoutes.ImazhOnboardingScreen.route) {
                    inclusive = true
                    saveState = true
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_imazh_onboarding),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp, vertical = 10.dp)

        ) {
            Text(
                text = stringResource(id = R.string.lbl_imazh),
                color = Cyan_200,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = stringResource(id = R.string.lbl_imazh_on_boarding_describe),
                color = Color_Text_2,
                style = MaterialTheme.typography.body1
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
        )
        Button(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
            onClick = {
                safeClick { viewModel.completeImazhOnBoarding() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 20.dp, bottom = 48.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_start),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}