package ai.ivira.app.features.hamahang.ui.new_audio.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Duplicate 3: Select file from storage
@Composable
fun HamahangUploadFileBottomSheet(
    submitAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_choose_file),
            style = MaterialTheme.typography.subtitle1,
            color = Color_Text_1,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.msg_hamahang_choose_file_desc),
            style = MaterialTheme.typography.body1.copy(lineHeight = 28.sp),
            color = Color_Text_2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                safeClick {
                    submitAction()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_hamahang_choose_file),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}