package ai.ivira.app.features.avasho.ui.file_creation

import ai.ivira.app.R
import ai.ivira.app.features.avasho.ui.file_creation.SpeakerTypeBottomSheet.MAN
import ai.ivira.app.features.avasho.ui.file_creation.SpeakerTypeBottomSheet.WOMAN
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary_200
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
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SelectSpeakerBottomSheet(
    modifier: Modifier = Modifier,
    fileName: String,
    uploadFileAction: (fileName: String, selected: SpeakerTypeBottomSheet) -> Unit
) {
    val radioOptions = remember {
        mutableStateListOf(MAN, WOMAN)
    }
    val (selectSpeaker, onSelectedSpeaker) = rememberSaveable { mutableStateOf(radioOptions[0]) }
    var textValue by rememberSaveable(fileName, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                fileName,
                selection = TextRange(0, fileName.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_determind_file_name),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color_Card, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraIcon(
                drawable = R.drawable.ic_rename,
                contentDescription = null,
                tint = Color_Text_3,
                modifier = Modifier.padding(top = 6.dp, start = 10.dp)
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

        Text(
            text = stringResource(id = R.string.lbl_speaker_type),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 28.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp)
        ) {
            radioOptions.forEach { text ->
                Text(
                    text = stringResource(id = text.type),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.safeClickable { onSelectedSpeaker(text) }
                )
                RadioButton(
                    selected = (text == selectSpeaker),
                    onClick = { safeClick { onSelectedSpeaker(text) } },
                    modifier = Modifier.weight(1f),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color_Primary_200,
                        unselectedColor = Color_Primary_200
                    )
                )
            }
        }

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                safeClick {
                    uploadFileAction(textValue.text, selectSpeaker)
                }
            },
            shape = RoundedCornerShape(8.dp),
            enabled = textValue.text.isNotBlank()
        ) {
            Text(
                text = stringResource(id = R.string.lbl_transform),
                style = MaterialTheme.typography.button,
                color = Color_Text_1
            )
        }
    }
}