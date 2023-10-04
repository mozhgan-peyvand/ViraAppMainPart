package ir.part.app.intelligentassistant.features.ava_negar.ui.details.sheets

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.ui.widgets.ViraIcon

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
            }) {
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
            }) {
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

@Preview
@Composable
private fun MenuDetailsScreenBottomSheetPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MenuDetailsScreenBottomSheet(
                onRenameAction = {},
                onRemoveFileAction = {}
            )
        }
    }
}