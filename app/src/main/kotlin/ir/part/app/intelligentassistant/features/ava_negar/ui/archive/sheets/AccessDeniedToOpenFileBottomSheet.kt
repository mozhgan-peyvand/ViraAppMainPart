package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_200
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun AccessDeniedToOpenFileBottomSheet(
    modifier: Modifier = Modifier,
    cancelAction: () -> Unit,
    submitAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_file),
                contentDescription = null,
                Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.lbl_access_file),
                style = MaterialTheme.typography.h6,
                color = Color_Text_1
            )
        }
        Text(
            text = stringResource(id = R.string.lbl_vira_need_to_file_permission),
            Modifier.padding(top = 16.dp),
            color = Color_Text_2
        )
        Text(
            text = stringResource(id = R.string.lbl_enable_permission_manually),
            color = Color_Text_2
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Button(
                onClick = {
                    safeClick {
                        submitAction()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_setting),
                    style = MaterialTheme.typography.button,
                    color = Color_Text_1
                )
            }
            Button(
                onClick = {
                    safeClick {
                        cancelAction()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_200
                )
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel),
                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AccessDeniedToOpenFileBottomSheetPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AccessDeniedToOpenFileBottomSheet(
                cancelAction = {},
                submitAction = {}
            )
        }
    }
}