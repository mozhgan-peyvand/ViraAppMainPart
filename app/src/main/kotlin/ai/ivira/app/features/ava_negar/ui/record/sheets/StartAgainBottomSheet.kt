package ai.ivira.app.features.ava_negar.ui.record.sheets

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Red_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun StartAgainBottomSheet(
    actionCancel: () -> Unit,
    actionStartAgain: () -> Unit
) {
    val eventHandler = LocalEventHandler.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            style = MaterialTheme.typography.h6,
            color = Color_Text_1,
            text = stringResource(id = R.string.lbl_start_again)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            style = MaterialTheme.typography.body1,
            color = Color_Text_2,
            text = stringResource(id = R.string.msg_if_start_again_file_will_be_deleted)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            TextButton(
                contentPadding = PaddingValues(vertical = 14.dp),
                onClick = {
                    safeClick {
                        eventHandler.specialEvent(AvanegarAnalytics.selectStartOver)
                        actionStartAgain()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_start_again),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300
                )
            }

            TextButton(
                contentPadding = PaddingValues(vertical = 14.dp),
                onClick = {
                    safeClick {
                        actionCancel()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel),
                    style = MaterialTheme.typography.button,
                    color = Color_Red
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
fun BottomSheetContentBottomSheetContentStartAgainPreview() {
    ViraPreview {
        StartAgainBottomSheet(
            actionCancel = {},
            actionStartAgain = {}
        )
    }
}