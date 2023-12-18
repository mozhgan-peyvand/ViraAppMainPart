package ai.ivira.app.features.home.ui.home.version.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun UpToDateBottomSheet(
    onUnderstoodClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        ViraImage(
            drawable = R.drawable.ic_vira_update,
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_vira_is_updated),
            style = MaterialTheme.typography.h6,
            color = Color_White
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_have_latest_version_wait_for_update),
            style = MaterialTheme.typography.body2,
            color = Color_Text_1,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(28.dp))

        TextButton(
            contentPadding = PaddingValues(horizontal = 44.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color_Primary),
            onClick = {
                safeClick { onUnderstoodClick() }
            }
        ) {
            Text(
                text = stringResource(id = R.string.lbl_understood),
                style = MaterialTheme.typography.button,
                color = Color_White
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun UpToDateBottomSheetPreview() {
    ViraPreview {
        UpToDateBottomSheet({})
    }
}