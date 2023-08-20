package ir.part.app.intelligentassistant.ui.screen.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.theme.Color_Card
import ir.part.app.intelligentassistant.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.ui.theme.Color_Red
import ir.part.app.intelligentassistant.ui.theme.Color_Red_Opacity_15
import ir.part.app.intelligentassistant.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme

@Composable
fun BottomSheetContentStartAgain(
    actionCancel: () -> Unit,
    actionStartAgain: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color_Card)
    ) {

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
                onClick = actionStartAgain,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_start_again),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300,
                )
            }

            TextButton(
                contentPadding = PaddingValues(vertical = 14.dp),
                onClick = actionCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15
                ),
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

@Preview
@Composable
fun BottomSheetContentBottomSheetContentStartAgainPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BottomSheetContentStartAgain(
                {},
                {}
            )
        }
    }
}
