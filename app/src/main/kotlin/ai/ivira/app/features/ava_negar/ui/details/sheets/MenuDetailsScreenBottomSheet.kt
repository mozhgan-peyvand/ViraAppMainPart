package ai.ivira.app.features.ava_negar.ui.details.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MenuDetailsScreenBottomSheet(
    modifier: Modifier = Modifier,
    onRenameAction: () -> Unit,
    onRemoveFileAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        TextButton(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 12.dp),
            contentPadding = PaddingValues(12.dp),
            onClick = {
                safeClick {
                    onRenameAction()
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ViraIcon(
                    drawable = R.drawable.icon_documents,
                    contentDescription = null,
                    tint = Color_Text_3
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = R.string.lbl_change_file_name),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Text_2
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )

        Spacer(modifier = Modifier.size(12.dp))

        TextButton(
            modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
            contentPadding = PaddingValues(12.dp),
            onClick = {
                safeClick { onRemoveFileAction() }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ViraIcon(
                    drawable = R.drawable.icon_trash_delete,
                    contentDescription = null,
                    tint = Color_Red
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = R.string.lbl_btn_delete_file),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Red
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun MenuDetailsScreenBottomSheetPreview() {
    ViraPreview {
        MenuDetailsScreenBottomSheet(
            onRenameAction = {},
            onRemoveFileAction = {}
        )
    }
}