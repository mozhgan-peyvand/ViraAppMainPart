package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun RenameFileBottomSheet(
    fileName: String,
    shouldShowKeyBoard: Boolean,
    reNameAction: (fileName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var textValue by rememberSaveable(fileName, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                fileName,
                selection = TextRange(0, fileName.length)
            )
        )
    }

    LaunchedEffect(shouldShowKeyBoard) {
        if (shouldShowKeyBoard) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 20.dp,
                top = 16.dp
            )
    ) {
        Text(
            text = stringResource(id = R.string.lbl_change_name),
            style = MaterialTheme.typography.h6,
            color = Color_Text_1
        )

        Spacer(modifier = Modifier.size(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color_Card, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraIcon(
                drawable = R.drawable.ic_rename,
                contentDescription = null,
                tint = Color_Text_3,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_file_name),
                    color = Color_Text_3,
                    style = MaterialTheme.typography.caption
                )

                BasicTextField(
                    value = textValue,
                    singleLine = true,
                    onValueChange = { textValue = it },
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    textStyle = MaterialTheme.typography.body1.copy(color = Color_Text_2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        }

        Spacer(modifier = Modifier.size(28.dp))

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            onClick = {
                safeClick {
                    reNameAction(textValue.text)
                }
            },
            shape = RoundedCornerShape(8.dp),
            enabled = textValue.text.isNotBlank()
        ) {
            Text(
                text = stringResource(id = R.string.lbl_save),
                style = MaterialTheme.typography.button,
                color = Color_Text_1
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun RenameFileBottomSheetPreview() {
    ViraPreview {
        RenameFileBottomSheet(
            fileName = "FileName",
            shouldShowKeyBoard = false,
            reNameAction = {}
        )
    }
}