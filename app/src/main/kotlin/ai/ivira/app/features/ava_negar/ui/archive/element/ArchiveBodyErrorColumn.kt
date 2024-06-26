package ai.ivira.app.features.ava_negar.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ArchiveBodyErrorColumn(
    isNetworkAvailable: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ErrorMessage(
            isNetworkAvailable = isNetworkAvailable,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
        )

        if (isNetworkAvailable) {
            TextButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15
                ),
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 26.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
                onClick = {
                    safeClick {
                        onTryAgainClick()
                    }
                }
            ) {
                Row(
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.lbl_try_again),
                        style = MaterialTheme.typography.button,
                        color = Color_Primary_300
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    ViraIcon(
                        drawable = R.drawable.ic_retry,
                        contentDescription = null,
                        tint = Color_Primary_300
                    )
                }
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun ArchiveBodyErrorColumnPreview() {
    ViraPreview {
        ArchiveBodyErrorColumn(
            isNetworkAvailable = true,
            onTryAgainClick = {}
        )
    }
}