package ir.part.app.intelligentassistant.ui.screen.archive.entity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        BottomSheetDetailItemBody(text = AIResource.string.lbl_copy_text_file) {
            copyItemAction()
        }
        BottomSheetDetailItemBody(text = AIResource.string.lbl_share_file) {
            shareItemAction()
        }
        BottomSheetDetailItemBody(text = AIResource.string.lbl_change_file_name) {
            renameItemAction()
        }
        BottomSheetDetailItemBody(text = AIResource.string.lbl_delete_file) {
            deleteItemAction()
        }
    }
}

@Composable
fun BottomSheetDetailItemBody(
    modifier: Modifier = Modifier, text: Int,
    onItemClick: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick() }) {
        Text(
            text = stringResource(id = text),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.LightGray
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
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_share_file),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = stringResource(id = AIResource.string.choose_format),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        BottomSheetDetailItemBody(
            stringResource(id = AIResource.string.lbl_share_with_word),
            painterResource(id = AIResource.drawable.icon_word)
        ) {
            onWordClick()
        }
        BottomSheetDetailItemBody(
            stringResource(id = AIResource.string.lbl_share_with_pdf),
            painterResource(id = AIResource.drawable.icon_pdf)
        ) {
            onPdfClick()
        }
        BottomSheetDetailItemBody(
            stringResource(id = AIResource.string.lbl_text_without_change),
            painterResource(id = AIResource.drawable.icon_text)
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
            modifier = Modifier.padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = icon, contentDescription = "icon"
            )
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color.LightGray
        )

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
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_name),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextField(value = fileName, onValueChange = {
            onValueChange(it)
        }, modifier.fillMaxWidth())
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp),
            onClick = {
                reNameAction()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black, contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = stringResource(id = AIResource.string.lbl_save))
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
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_name),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextField(value = fileName, onValueChange = {
            onValueChange(it)
        }, modifier.fillMaxWidth())
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp),
            onClick = {
                reNameAction()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black, contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = stringResource(id = AIResource.string.lbl_save))
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
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_file),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_you_can_only_choose_one_file),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_allowed_format),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
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
                backgroundColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = stringResource(id = AIResource.string.lbl_button_upload_new_file))
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
            .padding(vertical = 10.dp, horizontal = 15.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_delete_file),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Text(
            text = stringResource(
                id = AIResource.string.lbl_ask_delete_file,
                fileName
            ),
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_delete_are_you_sure),
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp),
                onClick = {
                    cancelAction()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(2.dp)

            ) {
                Text(text = stringResource(id = AIResource.string.lbl_btn_cancel))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp),
                onClick = {
                    deleteAction()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(text = stringResource(id = AIResource.string.lbl_btn_delete))
            }
        }
    }
}