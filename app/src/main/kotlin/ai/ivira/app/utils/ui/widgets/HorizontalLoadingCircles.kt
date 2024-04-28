package ai.ivira.app.utils.ui.widgets

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalContentColor
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

@Composable
fun HorizontalLoadingCircles(
    radius: Int,
    count: Int,
    padding: Int,
    modifier: Modifier = Modifier,
    color: Color = contentColorFor(backgroundColor = LocalContentColor.current)
) {
    val transition = rememberInfiniteTransition(label = "transition")
    val offsetList = remember { mutableListOf<State<Float>>() }
    val baseDelay = 200

    if (offsetList.isEmpty()) {
        for (i in 0 until count) {
            offsetList.add(
                transition.animateFloat(
                    initialValue = 0f,
                    targetValue = radius.toFloat(),
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(count * baseDelay),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(
                            offsetMillis = i * baseDelay,
                            offsetType = StartOffsetType.FastForward
                        )
                    ),
                    label = "offset"
                )
            )
        }
    }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val length = remember { radius / 2 + (count - 1) * (padding + radius) }
        val startOffset = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(length) {
            with(density) {
                startOffset.floatValue = (maxWidth.toPx() - length) / 2
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            onDraw = {
                for (i in 0 until count) {
                    drawCircle(
                        color = color,
                        radius = offsetList[i].value,
                        center = Offset(
                            x = startOffset.floatValue + (2 * radius * i) + (padding * i),
                            y = 0f
                        )
                    )
                }
            }
        )
    }
}