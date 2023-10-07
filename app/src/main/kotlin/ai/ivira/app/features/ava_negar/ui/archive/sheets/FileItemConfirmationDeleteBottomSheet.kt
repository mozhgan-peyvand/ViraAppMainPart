package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red_Opacity_15
import ai.ivira.app.utils.ui.theme.ViraTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun FileItemConfirmationDeleteBottomSheet(
    modifier: Modifier = Modifier,
    deleteAction: () -> Unit,
    cancelAction: () -> Unit,
    fileName: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_delete_file),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(
                id = R.string.lbl_ask_delete_file,
                fileName
            ),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    safeClick {
                        cancelAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp)

            ) {
                Text(
                    text = stringResource(id = R.string.lbl_btn_cancel),
                    style = MaterialTheme.typography.button
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    safeClick {
                        deleteAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_btn_delete),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@Preview
@Composable
private fun FileItemConfirmationDeleteBottomSheetPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            FileItemConfirmationDeleteBottomSheet(
                modifier = Modifier,
                deleteAction = {},
                cancelAction = {},
                fileName = "FileName"
            )
        }
    }
}