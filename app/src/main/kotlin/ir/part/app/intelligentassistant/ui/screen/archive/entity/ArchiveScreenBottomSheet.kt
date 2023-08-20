package ir.part.app.intelligentassistant.ui.screen.archive.entity

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.ui.theme.Color_Red_Opacity_15
import ir.part.app.intelligentassistant.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.ui.theme.Color_White
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
            modifier = Modifier.padding(8.dp)
        )
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
    onPdfClick: () -> Unit,
    onWordClick: () -> Unit,
    onOnlyTextClick: () -> Unit
) {
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
            stringResource(id = AIResource.string.lbl_share_with_word),
            painterResource(id = AIResource.drawable.ic_word_new)
        ) {
            onWordClick()
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

@Composable
fun BottomSheetDetailItemBody(
    text: String, icon: Painter, modifier: Modifier = Modifier,
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
                painter = icon, contentDescription = "icon",
                modifier = Modifier.padding(8.dp)
            )
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
    onValueChange: (String) -> Unit,
    reNameAction: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            value = fileName, onValueChange = {
                onValueChange(it)
            },
            modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
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
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            onClick = {
                reNameAction()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp)
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
    reNameAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
            style = MaterialTheme.typography.h6
        )
        Row(
            modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(11.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = AIResource.drawable.ic_rename),
                contentDescription = "",
            )
            Column(modifier.weight(2f)) {
                Text(
                    text = stringResource(id = AIResource.string.lbl_file_name),
                    color = Color_Text_3,
                    style = MaterialTheme.typography.caption
                )
                TextField(
                    value = fileName,
                    onValueChange = {
                        onValueChange(it)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        backgroundColor = Color.Black, textColor = Color_Text_2
                    ),
                    textStyle = MaterialTheme.typography.body1
                )
            }

        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                reNameAction()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color_White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = AIResource.string.lbl_save),
                style = MaterialTheme.typography.button
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
fun DeleteFileItemBottomSheet(
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
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
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
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 8.dp),
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