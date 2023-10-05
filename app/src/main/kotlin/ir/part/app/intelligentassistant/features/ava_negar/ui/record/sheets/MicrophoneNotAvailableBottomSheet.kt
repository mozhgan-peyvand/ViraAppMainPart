package ir.part.app.intelligentassistant.features.ava_negar.ui.record.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.ui.widgets.ViraImage

@Composable
fun MicrophoneNotAvailableBottomSheet(
    onDismissClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraImage(
                drawable = R.drawable.ic_record_audio,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.lbl_microphone),
                style = MaterialTheme.typography.h6,
                color = Color_Text_1
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.lbl_microphone_is_being_used),
            color = Color_Text_2
        )
        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                safeClick(onDismissClick)
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_understood),
                style = MaterialTheme.typography.button,
                color = Color_Text_1
            )
        }
    }
}

@Preview
@Composable
fun MicrophoneNotAvailableBottomSheetPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MicrophoneNotAvailableBottomSheet(
                onDismissClick = {}
            )
        }
    }
}