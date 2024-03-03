package ai.ivira.app.features.ava_negar.ui.archive

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DeleteBottomSheet(
    fileName: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (fileName.isNotBlank()) {
            Text(
                text = fileName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
                color = Color_Text_1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        TextButton(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
            contentPadding = PaddingValues(12.dp),
            onClick = {
                safeClick {
                    onDelete()
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_recycle_bin,
                    contentDescription = null,
                    tint = Color_Red
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = R.string.lbl_delete_file),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Red
                )
            }
        }
    }
}