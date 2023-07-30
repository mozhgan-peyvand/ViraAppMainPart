package ir.part.app.intelligentassistant.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme


@Composable
private fun BottomSheetContentBackToRecordingScreen(
    actionConvertFile: () -> Unit,
    actionDeleteFile: () -> Unit,
    actionClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 32.dp, start = 32.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f), text = stringResource(id = R.string.lbl_back)
            )
            TextButton(modifier = Modifier.size(32.dp), onClick = actionClose) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 8.dp, end = 32.dp, start = 32.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colors.onSurface)
        )

        Text(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.msg_if_click_back_file_will_be_deleted)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
        ) {

            TextButton(
                onClick = actionDeleteFile, modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(text = stringResource(id = R.string.lbl_convert_to_file))
            }

            Button(
                onClick = actionConvertFile, modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_delete_file),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

        }
    }
}

@Composable
private fun BottomSheetContentStartAgain(
    actionCancel: () -> Unit,
    actionStartAgain: () -> Unit,
    actionClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 32.dp, start = 32.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f), text = stringResource(id = R.string.lbl_start_again)
            )
            TextButton(modifier = Modifier.size(32.dp), onClick = actionClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }
        Divider(
            modifier = Modifier
                .padding(top = 8.dp, end = 32.dp, start = 32.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colors.onSurface)
        )

        Text(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.msg_if_start_again_file_will_be_deleted)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
        ) {

            TextButton(
                onClick = actionCancel, modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(text = stringResource(id = R.string.lbl_cancel))
            }

            Button(
                onClick = actionStartAgain, modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_start_again),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

        }
    }
}

@Preview
@Composable
fun BottomSheetContentBackToRecordingScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BottomSheetContentBackToRecordingScreen(
                {},
                {},
                {}
            )
        }

    }
}

@Preview
@Composable
fun BottomSheetContentBottomSheetContentStartAgainPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BottomSheetContentStartAgain(
                {},
                {},
                {}
            )
        }

    }
}