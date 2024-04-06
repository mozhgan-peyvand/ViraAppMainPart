package ai.ivira.app.features.hamahang.ui.archive

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.sheets.DetailItemBottomSheet
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HamahangProcessedWithDownloadBottomSheet(
    title: String,
    saveAudioFile: () -> Unit,
    downloadAudioFile: () -> Unit,
    shareItemAction: () -> Unit,
    renameItemAction: () -> Unit,
    deleteItemAction: () -> Unit,
    isFileDownloaded: Boolean,
    isFileDownloading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            color = Color_Text_1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        )
        if (!isFileDownloading && !isFileDownloaded) {
            HamahangArchiveProcessedItemBodyBottomSheet(
                text = R.string.lbl_download_audio_file,
                icon = R.drawable.ic_download_audio,
                onItemClick = { downloadAudioFile() }
            )
        } else if (isFileDownloaded) {
            HamahangArchiveProcessedItemBodyBottomSheet(
                text = R.string.lbl_save_audio_file,
                icon = R.drawable.ic_download_audio,
                onItemClick = { saveAudioFile() }
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color_OutLine
            )
            HamahangArchiveProcessedItemBodyBottomSheet(
                text = R.string.lbl_share_file,
                icon = R.drawable.ic_share_new,
                onItemClick = {
                    shareItemAction()
                }
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )
        HamahangArchiveProcessedItemBodyBottomSheet(
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
        HamahangArchiveProcessedItemBodyBottomSheet(
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
private fun HamahangArchiveProcessedItemBodyBottomSheet(
    text: Int,
    icon: Int,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color_Text_2,
    iconColor: Color = Color_Text_3
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .safeClickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically

    ) {
        ViraIcon(
            drawable = icon,
            contentDescription = null,
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

@ViraDarkPreview
@Composable
private fun ProcessedWithDownloadBottomSheetPreview() {
    ViraPreview {
        DetailItemBottomSheet(
            text = "",
            copyItemAction = {},
            shareItemAction = {},
            renameItemAction = {},
            deleteItemAction = {}
        )
    }
}