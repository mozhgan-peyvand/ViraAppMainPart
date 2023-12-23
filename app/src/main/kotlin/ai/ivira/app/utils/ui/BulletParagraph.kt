package ai.ivira.app.utils.ui

import ai.ivira.app.utils.ui.widgets.AutoTextSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BulletParagraph(
    text: String,
    color: Color,
    textStyle: TextStyle
) {
    Row {
        with(LocalDensity.current) {
            // this box is acting as a character, so it's sized with font scaling (sp)
            Box(
                modifier = Modifier
                    .size(6.sp.toDp(), 6.sp.toDp())
                    .alignBy {
                        // Add an alignment "baseline" 1sp below the bottom of the circle
                        9.sp.roundToPx()
                    }
                    .background(color, CircleShape)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        AutoTextSize(
            text = text,
            color = color,
            textScale = 0.9f,
            style = textStyle,
            modifier = Modifier
                .weight(1f)
                .alignBy(FirstBaseline)
        )
    }
}