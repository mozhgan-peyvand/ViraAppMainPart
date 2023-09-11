package ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.HorizontalPagerIndicator
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.utils.ui.theme.Color_On_Surface_Variant
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_200
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun AvaNegarOnboardingScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val pages = listOf(
        OnboardingItem.First(context),
        OnboardingItem.Second(context),
        OnboardingItem.Third(context)
    )

    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            navController.navigate(ScreenRoutes.AvaNegarArchiveList.route) {
                popUpTo(ScreenRoutes.AvaNegarOnboarding.route) {
                    inclusive = true
                    saveState = true
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) {

        Row(modifier = Modifier.weight(0.08f)) {
            AnimatedVisibility(pagerState.currentPage != 2) {
                TextButton(
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    modifier = Modifier
                        .padding(top = 12.dp, end = 20.dp)
                        .background(Color_Card, RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(32.dp),
                            color = Color_Card_Stroke,
                        ),
                    onClick = {
                        viewModel.navigateArchiveListScreen()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.lbl_skip),
                        style = MaterialTheme.typography.button,
                        color = Color_Text_1
                    )
                }
            }
        }

        //disable ripple effect
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(0.6f)
            ) { position ->
                MainOnBoardingItemBody(
                    modifier = Modifier.fillMaxSize(),
                    onBoardingItem = pages[position]
                )
            }
        }
        
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = 3,
            activeColor = Color_Primary_200,
            inactiveColor = Color_On_Surface_Variant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        FinishButton(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .weight(0.2f),
            pagerState = pagerState,
            onClick = {
                viewModel.navigateArchiveListScreen()
            }
        )
    }
}

@Composable
private fun MainOnBoardingItemBody(
    modifier: Modifier = Modifier,
    onBoardingItem: OnboardingItem
) {
    Column(
        modifier = modifier.padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = onBoardingItem.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(bottom = 24.dp, top = 39.dp)
                .weight(0.5f)
        )

        Column(
            modifier = modifier
                .weight(0.5f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = onBoardingItem.title,
                style = MaterialTheme.typography.h5,
                color = Color_Primary_200,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = onBoardingItem.description,
                style = MaterialTheme.typography.body1,
                color = Color_Text_2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FinishButton(
    pagerState: PagerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 58.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible = pagerState.currentPage == 2
        ) {
            Button(
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onClick() }
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_start),
                    style = MaterialTheme.typography.button,
                    color = Color_Text_1
                )
            }
        }
    }
}

@Preview
@Composable
private fun AvaNegarOnboardingScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvaNegarOnboardingScreen(rememberNavController())
        }
    }
}