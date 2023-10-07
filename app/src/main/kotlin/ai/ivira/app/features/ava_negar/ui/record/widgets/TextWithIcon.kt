package ai.ivira.app.features.ava_negar.ui.record.widgets

import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp

@Composable
fun TextWithIcon(
    @StringRes text: Int,
    @DrawableRes icon: Int,
    textStyle: TextStyle = MaterialTheme.typography.caption,
    iconTint: Color = MaterialTheme.colors.onBackground
) {
    val myId = "inlineContent"
    val annotatedText = buildAnnotatedString {
        val raw = stringResource(id = text)
        val index = raw.indexOf("[icon]")

        append(raw.substring(0, index))

        appendInlineContent(myId, "[icon]")
        if (index + 6 < raw.length) {
            append(raw.substring(index + 6))
        }
    }

    val inlineContent = mapOf(
        myId to InlineTextContent(
            Placeholder(
                width = 30.sp,
                height = 30.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ViraIcon(
                    drawable = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.8f)
                )
            }
        }
    )

    Text(
        text = annotatedText,
        inlineContent = inlineContent,
        style = textStyle
    )
}