package ai.ivira.app.features.ava_negar.ui.record.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpSize

@Composable
fun ClickableTextWithDashUnderline(
    @StringRes textRes: Int,
    startIndex: Int,
    endIndex: Int,
    onClick: () -> Unit,
    textStyle: TextStyle,
    substringTextStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val text = stringResource(id = textRes)
    val subString = text.substring(startIndex = startIndex, endIndex = endIndex)

    val myId = "inlineContent"
    val annotatedText = buildAnnotatedString {
        val index = text.indexOf(subString)

        append(text.substring(0, index))

        appendInlineContent(myId, subString)

        if (index + subString.length < text.length) {
            append(text.substring(index + subString.length))
        }
    }

    val density = LocalDensity.current

    MeasureViewSize(
        viewToMeasure = {
            ViewToInline(
                subString = subString,
                substringTextStyle = substringTextStyle,
                onClick = onClick
            )
        }
    ) { measuredSize ->
        val inlineContent = mapOf(
            Pair(
                // This tells the [BasicText] to replace the placeholder string "[myBox]" by
                // the composable given in the [InlineTextContent] object.
                myId,
                InlineTextContent(
                    // Placeholder tells text layout the expected size and vertical alignment of
                    // children composable.
                    with(density) {
                        Placeholder(
                            width = measuredSize.width.toSp(),
                            height = measuredSize.height.toSp(),
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    }
                ) {
                    ViewToInline(subString, substringTextStyle, onClick)
                }
            )
        )

        Text(
            text = annotatedText,
            inlineContent = inlineContent,
            style = textStyle,
            modifier = modifier
        )
    }
}

@Composable
private fun ViewToInline(
    subString: String,
    substringTextStyle: TextStyle,
    onClick: () -> Unit
) {
    val width = remember { mutableFloatStateOf(0f) }
    val height = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .onGloballyPositioned {
                width.floatValue = it.size.width.toFloat()
                height.floatValue = it.size.height.toFloat()
            }
            .drawBehind {
                drawLine(
                    color = substringTextStyle.color,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f), 0f),
                    start = Offset(0f, height.floatValue),
                    end = Offset(width.floatValue, height.floatValue),
                    strokeWidth = 3.0f
                )
            }
    ) {
        Text(
            style = substringTextStyle,
            text = subString,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MeasureViewSize(
    viewToMeasure: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (DpSize) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val measuredSize = subcompose("viewToMeasure") {
            viewToMeasure()
        }[0].measure(constraints)
            .let {
                DpSize(
                    width = it.width.toDp(),
                    height = it.height.toDp()
                )
            }

        val contentPlaceable = subcompose("content") {
            content(measuredSize)
        }.firstOrNull()?.measure(constraints)

        layout(contentPlaceable?.width ?: 0, contentPlaceable?.height ?: 0) {
            contentPlaceable?.place(0, 0)
        }
    }
}