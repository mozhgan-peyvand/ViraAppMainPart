package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun ErrorMessage(
    isNetworkAvailable: Boolean,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        textAlign = textAlign,
        text = stringResource(
            id = if (isNetworkAvailable) R.string.msg_server_error
            else R.string.msg_upload_will_start_after_connect_to_internet
        ),
        style = MaterialTheme.typography.caption,
        color = Color_Red
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorMessagePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ErrorMessage(
                isNetworkAvailable = false,
                textAlign = TextAlign.Center,
            )
        }
    }
}
