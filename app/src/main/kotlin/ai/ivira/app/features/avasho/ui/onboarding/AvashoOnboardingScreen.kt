package ai.ivira.app.features.avasho.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.utils.ui.BulletParagraph
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
import ai.ivira.app.utils.ui.widgets.TextAutoSize
import ai.ivira.app.utils.ui.widgets.TextAutoSizeRange
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.HorizontalPagerIndicator

@Composable
fun AvashoOnboardingScreenRoute(navController: NavHostController) {
    AvashoOnboardingScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun AvashoOnboardingScreen(
    navController: NavHostController,
    viewModel: AvashoOnboardingViewModel
) {
    val context = LocalContext.current
    val pages = listOf(
        AvashoOnboardingItem.First(context),
        AvashoOnboardingItem.Second(context)
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    LaunchedEffect(viewModel.shouldNavigate.value) {
        if (viewModel.shouldNavigate.value) {
            navController.navigate(ScreenRoutes.AvaShoArchiveScreen.route) {
                popUpTo(ScreenRoutes.AvaShoOnboardingScreen.route) {
                    inclusive = true
                    saveState = true
                }
            }
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
            AnimatedVisibility(pagerState.currentPage == 0) {
                TextButton(
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    border = BorderStroke(width = 1.dp, color = Color_Card_Stroke),
                    shape = RoundedCornerShape(32.dp),
                    onClick = {
                        safeClick {
                            viewModel.navigateToArchiveListScreen()
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

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(0.6f)
            ) { position ->
                AvashoOnBoardingItemBody(
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
                viewModel.navigateToArchiveListScreen()
            },
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .weight(0.1f)
        )
    }
}

@Composable
private fun AvashoOnBoardingItemBody(
    onBoardingItem: AvashoOnboardingItem,
    modifier: Modifier = Modifier
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
                text = onBoardingItem.title,
                style = MaterialTheme.typography.h5,
                color = Color_Primary_200,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(16.dp))

            if (onBoardingItem.description.size == 1) {
                TextAutoSize(
                    text = onBoardingItem.description.first(),
                    style = MaterialTheme.typography.body1,
                    color = Color_Text_2,
                    textScale = TextAutoSizeRange(
                        min = 10.sp,
                        max = MaterialTheme.typography.body1.fontSize
                    ),
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
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        AnimatedVisibility(visible = pagerState.currentPage == 1) {
            Button(
                contentPadding = PaddingValues(vertical = 14.dp),
                onClick = {
                    safeClick { onClick() }
                },
                modifier = Modifier.fillMaxWidth()
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
private fun AvashoOnboardingScreenPreview() {
    ViraPreview {
        AvashoOnboardingScreenRoute(rememberNavController())
    }
}

@ViraDarkPreview
@Composable
private fun AvashoOnBoardingItemBodyPreview() {
    ViraPreview {
        AvashoOnBoardingItemBody(onBoardingItem = AvashoOnboardingItem.First(LocalContext.current))
    }
}