package ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.HorizontalPagerIndicator
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun AvaNegarOnboardingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onBoardingViewModel: OnboardingViewModel = hiltViewModel()
) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scrollState = rememberScrollState()

    val pages = listOf(
            OnboardingItem.First,
            OnboardingItem.Second,
            OnboardingItem.Third
    )

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        HorizontalPager(
            state = pagerState, Modifier.weight(10f)
        ) { position ->
            AvaNegarOnBoardingItemBody(onBoardingItem = pages[position])
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = 3,
            Modifier
                .align(CenterHorizontally)
                .weight(1f)
        )
        FinishButton(
            pagerState = pagerState,
            onClick = {
                onBoardingViewModel.saveOnBoardingState(completed = true)
                navController.popBackStack()
                navController.navigate(ScreenRoutes.AvaNegarArchiveList.route)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AvaNegarOnBoardingItemBody(
    modifier: Modifier = Modifier,
    onBoardingItem: OnboardingItem
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = onBoardingItem.image),
            contentDescription = null,
            modifier = Modifier.size(350.dp)
        )
        Text(
            text = stringResource(id = onBoardingItem.title),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = onBoardingItem.description),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )
    }
}

@Composable
private fun FinishButton(
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible =
            pagerState.currentPage == 2
        ) {
            Button(onClick = { onClick() }) {
                Text(text = stringResource(id = AIResource.string.lbl_start))
            }
        }
    }
}
