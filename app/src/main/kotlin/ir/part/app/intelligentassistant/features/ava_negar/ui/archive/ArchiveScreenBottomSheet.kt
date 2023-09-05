package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun BottomSheetDetailItem(
    modifier: Modifier = Modifier,
    text: String,
    copyItemAction: () -> Unit,
    shareItemAction: () -> Unit,
    renameItemAction: () -> Unit,
    deleteItemAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            color = Color_Text_1,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        )
        BottomSheetArchiveItemBody(
            text = AIResource.string.lbl_copy_text_file,
            icon = AIResource.drawable.ic_copy_new,
            onItemClick = { copyItemAction() }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        BottomSheetArchiveItemBody(
            text = AIResource.string.lbl_share_file,
            icon = AIResource.drawable.ic_share_new, onItemClick = {
                shareItemAction()
            }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        BottomSheetArchiveItemBody(
            text = AIResource.string.lbl_change_file_name,
            icon = AIResource.drawable.ic_rename,
            onItemClick = { renameItemAction() }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        BottomSheetArchiveItemBody(
            text = AIResource.string.lbl_delete_file,
            icon = AIResource.drawable.ic_removefile,
            onItemClick = {
                deleteItemAction()
            },
            textColor = MaterialTheme.colors.onError
        )
    }
}

@Composable
fun BottomSheetArchiveItemBody(
    modifier: Modifier = Modifier,
    text: Int,
    icon: Int,
    onItemClick: () -> Unit,
    textColor: Color = Color_Text_2
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 8.dp
            )
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.subtitle1,
            color = textColor
        )
    }
}

@Composable
fun BottomSheetShareDetailItem(
    modifier: Modifier = Modifier,
    isConverting: Boolean,
    onPdfClick: () -> Unit,
    onTextClick: () -> Unit,
    onOnlyTextClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        //TODO set appropriate lottie file
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_loading)
    )

    if (isConverting) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .padding(vertical = 80.dp)
                    .size(48.dp)
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = AIResource.string.lbl_share_file),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = stringResource(id = AIResource.string.choose_format),
                    style = MaterialTheme.typography.subtitle2
                )
            }
            BottomSheetDetailItemBody(
                stringResource(id = AIResource.string.lbl_share_with_Text),
                painterResource(id = AIResource.drawable.ic_text)
            ) {
                onTextClick()
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color_OutLine
            )
            BottomSheetDetailItemBody(
                stringResource(id = AIResource.string.lbl_share_with_pdf),
                painterResource(id = AIResource.drawable.ic_pdf_new)
            ) {
                onPdfClick()
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color_OutLine
            )
            BottomSheetDetailItemBody(
                stringResource(id = AIResource.string.lbl_text_without_change),
                painterResource(id = AIResource.drawable.ic_text_new)
            ) {
                onOnlyTextClick()
            }
        }
    }
}

@Composable
fun BottomSheetDetailItemBody(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onShareItemClick: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable { onShareItemClick() }) {
        Row(
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = icon,
                contentDescription = "icon",
                modifier = Modifier.padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 12.dp
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1, color = Color_Text_2
            )
        }
    }
}

@Composable
fun RenameFileBottomSheetContent(
    fileName: String,
    shouldShowKeyBoard: Boolean,
    renameAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var name by remember(fileName) { mutableStateOf(fileName) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(shouldShowKeyBoard) {
        if (shouldShowKeyBoard)
            focusRequester.requestFocus()
        else
            focusManager.clearFocus()
    }

    Column(
        modifier = modifier
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_name),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .focusRequester(focusRequester)
                .border(
                    1.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colors.primary
                ), textStyle = MaterialTheme.typography.body2,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.size(10.dp))

        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                renameAction(name)
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = name.isNotBlank()
        ) {
            Text(
                text = stringResource(id = AIResource.string.lbl_save),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Composable
fun RenameFile(
    fileName: String,
    onValueChange: (String) -> Unit,
    shouldShowKeyBoard: Boolean,
    reNameAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(shouldShowKeyBoard) {
        if (shouldShowKeyBoard)
            focusRequester.requestFocus()
        else
            focusManager.clearFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 16.dp),
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
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
            Image(
                painter = painterResource(id = AIResource.drawable.ic_rename),
                contentDescription = null,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(id = AIResource.string.lbl_file_name),
                    color = Color_Text_3,
                    style = MaterialTheme.typography.caption
                )

                BasicTextField(
                    value = fileName,
                    singleLine = true,
                    onValueChange = { onValueChange(it) },
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
            onClick = reNameAction,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = stringResource(id = AIResource.string.lbl_save),
                style = MaterialTheme.typography.button,
                color = Color_Text_1
            )
        }
    }
}

@Composable
fun ChooseFileBottomSheetContent(
    onOpenFile: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            text = stringResource(id = AIResource.string.lbl_choose_file),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_you_can_only_choose_one_file),
            style = MaterialTheme.typography.body1,
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_allowed_format),
            style = MaterialTheme.typography.body1,

            )
        Button(
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 30.dp,
                    vertical = 10.dp
                ),
            onClick = {
                onOpenFile()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = AIResource.string.lbl_button_upload_new_file),
                style = MaterialTheme.typography.button
            )
        }

    }

}

@Composable
fun DeleteFileItemConfirmationBottomSheet(
    modifier: Modifier = Modifier,
    deleteAction: () -> Unit,
    cancelAction: () -> Unit,
    fileName: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_delete_file),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(
                id = AIResource.string.lbl_ask_delete_file,
                fileName
            ),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 28.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(14.dp),
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    cancelAction()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp)

            ) {
                Text(
                    text = stringResource(id = AIResource.string.lbl_btn_cancel),
                    style = MaterialTheme.typography.button
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                contentPadding = PaddingValues(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    deleteAction()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = AIResource.string.lbl_btn_delete),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}