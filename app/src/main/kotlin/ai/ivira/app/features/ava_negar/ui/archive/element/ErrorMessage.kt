package ai.ivira.app.features.ava_negar.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_Red
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

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
            id = if (isNetworkAvailable) {
                R.string.msg_server_error
            } else {
                R.string.msg_upload_will_start_after_connect_to_internet
            }
        ),
        style = MaterialTheme.typography.caption,
        color = Color_Red
    )
}

@ViraDarkPreview
@Composable
private fun ErrorMessagePreview() {
    ViraPreview {
        ErrorMessage(
            isNetworkAvailable = false,
            textAlign = TextAlign.Center
        )
    }
}