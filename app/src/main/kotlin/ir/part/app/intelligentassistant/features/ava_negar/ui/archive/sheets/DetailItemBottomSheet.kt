package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme

@Composable
fun DetailItemBottomSheet(
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        )
        ArchiveItemBodyBottomSheet(
            text = R.string.lbl_copy_text_file,
            icon = R.drawable.ic_copy_new,
            onItemClick = { copyItemAction() }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        ArchiveItemBodyBottomSheet(
            text = R.string.lbl_share_file,
            icon = R.drawable.ic_share_new, onItemClick = {
                shareItemAction()
            }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        ArchiveItemBodyBottomSheet(
            text = R.string.lbl_change_file_name,
            icon = R.drawable.ic_rename,
            onItemClick = { renameItemAction() }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        ArchiveItemBodyBottomSheet(
            text = R.string.lbl_delete_file,
            icon = R.drawable.ic_removefile,
            onItemClick = {
                deleteItemAction()
            },
            textColor = Color_Red,
            iconColor = Color_Red
        )
    }
}


@Composable
private fun ArchiveItemBodyBottomSheet(
    modifier: Modifier = Modifier,
    text: Int,
    icon: Int,
    onItemClick: () -> Unit,
    textColor: Color = Color_Text_2,
    iconColor: Color = Color_Text_3
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = iconColor,
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

@Preview
@Composable
private fun DetailItemBottomSheetPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            DetailItemBottomSheet(
                text = "aaa",
                copyItemAction = {},
                shareItemAction = {},
                renameItemAction = {},
                deleteItemAction = {}
            )
        }
    }
}

@Preview
@Composable
private fun ArchiveItemBodyBottomSheetPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveItemBodyBottomSheet(
                text = R.string.lbl_share_file,
                icon = R.drawable.icon_share,
                onItemClick = {},
                textColor = Color_Text_2

            )
        }
    }
}