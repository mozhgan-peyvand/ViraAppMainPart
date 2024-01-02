package ai.ivira.app.features.home.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.features.home.ui.onboarding.MainOnboardingItem.First
import ai.ivira.app.features.home.ui.onboarding.MainOnboardingItem.Second
import ai.ivira.app.features.home.ui.onboarding.MainOnboardingItem.Third
import ai.ivira.app.utils.ui.BulletParagraph
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.widgets.AutoTextSize
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.HorizontalPagerIndicator

@Composable
fun HomeOnboardingScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(HomeAnalytics.screenViewOnboardingSlides)
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
    val context = LocalContext.current
    val pages = listOf(
        First(context),
        Second(context),
        Third(context)
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            viewModel.onBoardingShown()
            navController.popBackStack()
            navController.navigate(ScreenRoutes.Home.route)
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
            .padding(bottom = 16.dp)
    ) {
        Row(modifier = Modifier.weight(0.1f)) {
            AnimatedVisibility(pagerState.currentPage != 2) {
                TextButton(
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    border = BorderStroke(width = 1.dp, color = Color_Card_Stroke),
                    shape = RoundedCornerShape(32.dp),
                    onClick = {
                        safeClick {
                            eventHandler.onboardingEvent(HomeAnalytics.onboardingEnd)
                            viewModel.navigateToMainOnboarding()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 12.dp, end = 20.dp)
                        .background(Color_Card, RoundedCornerShape(32.dp))
                ) {
                    Text(
                        text = stringResource(R.string.lbl_skip),
                        style = MaterialTheme.typography.button,
                        color = Color_Text_1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(0.6f)
            ) { position ->
                MainOnBoardingItemBody(
                    onBoardingItem = pages[position],
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = pages.size,
            activeColor = Color_Primary_200,
            inactiveColor = Color_On_Surface_Variant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        FinishButton(
            pagerState = pagerState,
            onClick = {
                eventHandler.onboardingEvent(HomeAnalytics.onboardingEnd)
                viewModel.navigateToMainOnboarding()
            },
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .weight(0.1f)
        )
    }
}

@Composable
private fun MainOnBoardingItemBody(
    modifier: Modifier = Modifier,
    onBoardingItem: MainOnboardingItem
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 28.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ViraImage(
                drawable = onBoardingItem.image,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 286.dp, height = 253.dp)
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = onBoardingItem.title),
                style = MaterialTheme.typography.h5,
                color = Color_Primary_200,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(16.dp))

            if (onBoardingItem.description.size == 1) {
                AutoTextSize(
                    text = onBoardingItem.description.first(),
                    style = MaterialTheme.typography.body1,
                    color = Color_Text_2,
                    textScale = 0.9f,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                onBoardingItem.description.forEach { text ->
                    BulletParagraph(
                        text = text,
                        color = Color_Text_2,
                        textStyle = MaterialTheme.typography.body1
                    )
                }
            }
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
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(visible = pagerState.currentPage == 2) {
            Button(
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    safeClick {
                        onClick()
                    }
                }
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

@ViraDarkPreview
@Composable
private fun HomeOnboardingScreenPreview() {
    ViraPreview {
        HomeOnboardingScreenRoute(rememberNavController())
    }
}

@ViraDarkPreview
@Composable
private fun MainOnBoardingItemBodyPreview() {
    ViraPreview {
        MainOnBoardingItemBody(
            onBoardingItem = First(LocalContext.current)
        )
    }
}