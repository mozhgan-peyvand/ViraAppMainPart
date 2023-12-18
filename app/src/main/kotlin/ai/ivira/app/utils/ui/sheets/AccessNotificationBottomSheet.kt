package ai.ivira.app.utils.ui.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp

@Composable
fun AccessNotificationBottomSheet(
    isPermissionDeniedPermanently: () -> Boolean,
    onCancelClick: () -> Unit,
    onEnableClick: () -> Unit,
    onSettingClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_notification),
            style = MaterialTheme.typography.h6,
            color = Color_Text_1
        )

        ViraImage(
            drawable = R.drawable.img_bell,
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(
                id = if (isPermissionDeniedPermanently()) {
                    R.string.lbl_enable_notification_for_recording_when_screen_is_locked
                } else {
                    R.string.lbl_enable_notification_for_recording
                }
            ),
            style = MaterialTheme.typography.body2,
            color = Color_Text_1
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            TextButton(
                contentPadding = PaddingValues(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color_Primary),
                onClick = {
                    safeClick {
                        if (isPermissionDeniedPermanently()) {
                            onSettingClick()
                        } else {
                            onEnableClick()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(
                        id = if (isPermissionDeniedPermanently()) {
                            R.string.lbl_setting
                        } else {
                            R.string.lbl_enable
                        }
                    ),
                    style = MaterialTheme.typography.button,
                    color = Color_Text_1
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            TextButton(
                contentPadding = PaddingValues(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color_Primary_Opacity_15),
                onClick = {
                    safeClick {
                        onCancelClick()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_200
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun AccessNotificationBottomSheetPreview() {
    ViraPreview {
        AccessNotificationBottomSheet(
            isPermissionDeniedPermanently = { false },
            onCancelClick = {},
            onEnableClick = {},
            onSettingClick = {}
        )
    }
}