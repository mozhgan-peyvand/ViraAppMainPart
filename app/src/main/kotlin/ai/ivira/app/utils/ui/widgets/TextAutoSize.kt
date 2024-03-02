package ai.ivira.app.utils.ui.widgets

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextAutoSize(
    text: String,
    textScale: TextAutoSizeRange,
    modifier: Modifier = Modifier,
    maxLine: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    var fontSizeValue by remember(textScale) { mutableFloatStateOf(textScale.max.value) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        maxLines = maxLine,
        style = style,
        textAlign = textAlign,
        fontSize = fontSizeValue.sp,
        onTextLayout = onTextLayout@{ textResult ->
            if (!textResult.hasVisualOverflow) {
                readyToDraw = true
                return@onTextLayout
            }
            if (readyToDraw) {
                return@onTextLayout
            }

            val nextFontSizeValue = fontSizeValue - textScale.step.value
            if (nextFontSizeValue <= textScale.step.value) {
                fontSizeValue = textScale.min.value
                readyToDraw = true
            } else {
                fontSizeValue = nextFontSizeValue
            }
        },
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        }
    )
}

data class TextAutoSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP
) {
    companion object {
        val DEFAULT_TEXT_STEP = 1.sp
    }
}