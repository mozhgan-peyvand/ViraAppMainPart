package ai.ivira.app.utils.ui.widgets

import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive

private const val SCROLL_DURATION = 5_000L

@Composable
fun HorizontalInfinitePager(
    realItemSize: Int,
    itemContent: @Composable (position: Int) -> Unit,
    modifier: Modifier = Modifier,
    slideDirection: LayoutDirection = LayoutDirection.Rtl
) {
    CompositionLocalProvider(LocalLayoutDirection provides slideDirection) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val infinitePageCount = Int.MAX_VALUE
            val middlePage = infinitePageCount / 2
            val pagerState = rememberPagerState(
                initialPage = middlePage - (middlePage % realItemSize),
                initialPageOffsetFraction = 0f
            ) {
                infinitePageCount
            }
            val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()
            val isPressState = pagerState.interactionSource.collectIsPressedAsState()

            HorizontalPager(
                state = pagerState,
                pageSpacing = 8.dp,
                modifier = Modifier.fillMaxSize()
            ) { item ->

                val page by remember {
                    mutableIntStateOf(item % realItemSize)
                }
                itemContent(page)
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                pageCount = realItemSize,
                pageIndexMapping = { it % realItemSize },
                activeColor = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .align(Alignment.BottomCenter)
            )

            LaunchedEffect(isDraggedState, isPressState) {
                snapshotFlow { isDraggedState.value && isPressState.value }
                    .collectLatest { isMove ->
                        if (!isMove) {
                            while (isActive) {
                                delay(SCROLL_DURATION)
                                runCatching {
                                    pagerState.animateScrollToPage(pagerState.currentPage.inc() % pagerState.pageCount)
                                }
                            }
                        }
                    }
            }
        }
    }
}