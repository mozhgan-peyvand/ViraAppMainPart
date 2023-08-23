package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme


@Composable
fun ArchiveProcessedFileElementColumn(
    archiveViewProcessed: AvanegarProcessedFileView,
    onItemClick: (Int) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit
) {
    Card(
        backgroundColor = Color_Card,
        border = BorderStroke(
            0.5.dp,
            if (archiveViewProcessed.isSeen) Color_Card_Stroke else MaterialTheme.colors.primary
        ),
        modifier = Modifier.height(108.dp),
        onClick = {
            onItemClick(archiveViewProcessed.id)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = if (archiveViewProcessed.isSeen) Color_Text_1
                    else MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.title
                )
                IconButton(
                    onClick = { onMenuClick(archiveViewProcessed) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                color = Color_Text_2,
                style = MaterialTheme.typography.body2,
                text = archiveViewProcessed.text
            )

            Spacer(modifier = Modifier.size(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Icon(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = null,
                    tint = Color_Primary_300
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    color = Color_Text_3,
                    style = MaterialTheme.typography.caption,
                    text = archiveViewProcessed.createdAt
                )
            }
        }
    }
}

@Preview
@Composable
private fun ArchiveProcessedFileElementColumnPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveProcessedFileElementColumn(
                archiveViewProcessed = AvanegarProcessedFileView(
                    id = 0,
                    title = "عنوان",
                    text = "متن متن متن متن متن متن",
                    createdAt = "54654",
                    filePath = "SASAS",
                    isSeen = true,
                ),
                onItemClick = {},
                onMenuClick = {},
            )
        }
    }
}
