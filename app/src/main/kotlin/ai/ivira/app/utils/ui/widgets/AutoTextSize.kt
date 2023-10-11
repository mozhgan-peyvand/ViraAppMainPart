package ai.ivira.app.utils.ui.widgets

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AutoTextSize(
    text: String,
    textScale: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    var multiplier by remember { mutableFloatStateOf(1f) }

    Text(
        text = text,
        color = color,
        maxLines = 1,
        style = style.copy(
            fontSize = LocalTextStyle.current.fontSize * multiplier,
            lineHeight = LocalTextStyle.current.lineHeight * multiplier
        ),
        onTextLayout = {
            if (it.hasVisualOverflow) {
                multiplier = textScale
            }
        },
        modifier = modifier
    )
}