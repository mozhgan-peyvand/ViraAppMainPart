package ai.ivira.app.utils.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LazyListState.isScrollingUp(): State<Boolean> {
    var lastIndex by remember { mutableIntStateOf(0) }
    var lastScroll by remember { mutableIntStateOf(0) }
    var scrollState by remember { mutableStateOf(true) }

    return remember {
        derivedStateOf {
            if (firstVisibleItemIndex != lastIndex || firstVisibleItemScrollOffset != lastScroll) {
                scrollState = firstVisibleItemIndex < lastIndex ||
                    (firstVisibleItemIndex == lastIndex && firstVisibleItemScrollOffset < lastScroll)
                lastIndex = firstVisibleItemIndex
                lastScroll = firstVisibleItemScrollOffset
            }
            scrollState
        }
    }
}