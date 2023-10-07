package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun RenameFileContentBottomSheet(
    fileName: String,
    shouldShowKeyBoard: Boolean,
    renameAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by rememberSaveable(fileName, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                fileName,
                selection = TextRange(0, fileName.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(shouldShowKeyBoard) {
        if (shouldShowKeyBoard) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = modifier
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_change_name),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.lbl_choose_name),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = textValue,
            onValueChange = {
                textValue = it
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .focusRequester(focusRequester)
                .border(
                    1.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colors.primary
                ),
            textStyle = MaterialTheme.typography.body2.copy(
                fontFamily = FontFamily(
                    Font(R.font.bahij_helvetica_neue_vira_edition_roman)
                )
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.size(10.dp))

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                safeClick {
                    renameAction(textValue.text)
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = textValue.text.isNotBlank()
        ) {
            Text(
                text = stringResource(id = R.string.lbl_save),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Preview
@Composable
private fun RenameFileContentBottomSheetPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            RenameFileContentBottomSheet(
                fileName = "FileName",
                shouldShowKeyBoard = false,
                renameAction = {}
            )
        }
    }
}