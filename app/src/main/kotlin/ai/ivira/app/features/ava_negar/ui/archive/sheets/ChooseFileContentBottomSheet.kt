package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_White
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ChooseFileContentBottomSheet(
    onOpenFile: () -> Unit,
    modifier: Modifier = Modifier,
    descriptionFileFormat: Int,
    descriptionTimeNeed: Int? = null
) {
    val eventHandler = LocalEventHandler.current

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 10f,
                    topEnd = 10f,
                    bottomEnd = 0f,
                    bottomStart = 0f
                )
            )
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_choose_file),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = stringResource(id = R.string.lbl_you_can_only_choose_one_file),
            style = MaterialTheme.typography.body1
        )
        Text(
            text = stringResource(id = descriptionFileFormat),
            style = MaterialTheme.typography.body1
        )
        descriptionTimeNeed?.let {
            Text(
                text = stringResource(id = descriptionTimeNeed),
                style = MaterialTheme.typography.body1
            )
        }

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 30.dp,
                    vertical = 10.dp
                ),
            onClick = {
                safeClick {
                    eventHandler.selectItem(AvanegarAnalytics.selectChooseFile)
                    onOpenFile()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.lbl_button_upload_new_file),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun ChooseFileContentBottomSheetPreview() {
    ViraPreview {
        ChooseFileContentBottomSheet(
            onOpenFile = {},
            descriptionFileFormat = 0,
            descriptionTimeNeed = 0
        )
    }
}