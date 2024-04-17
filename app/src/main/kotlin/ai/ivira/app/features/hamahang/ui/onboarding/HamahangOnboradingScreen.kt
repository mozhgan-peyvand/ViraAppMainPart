package ai.ivira.app.features.hamahang.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
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

@Composable
fun HamahangOnboardingRoute(navController: NavController) {
    HamahangOnboardingScreen(
        viewModel = hiltViewModel(),
        navigateToArchive = {
            navController.navigate(HamahangScreenRoutes.HamahangArchiveListScreen.route) {
                popUpTo(HamahangScreenRoutes.HamahangOnboardingScreen.route) {
                    inclusive = true
                    saveState = true
                }
            }
        }
    )
}

@Composable
private fun HamahangOnboardingScreen(
    viewModel: HamahangOnbordingViewModel,
    navigateToArchive: () -> Unit
) {
    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            navigateToArchive()
        }
    }

    HamahangOnboardingUI(completeImazhOnBoarding = { viewModel.completeImazhOnBoarding() })
}

@Composable
private fun HamahangOnboardingUI(completeImazhOnBoarding: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_hamahang_onboarding),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        )
        Column(modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)) {
            Text(
                text = stringResource(id = R.string.lbl_hamahang),
                color = Cyan_200,
                style = MaterialTheme.typography.h5
            )
            Text(
                text = stringResource(id = R.string.lbl_hamahang_on_boarding_describe),
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
                safeClick(completeImazhOnBoarding)
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