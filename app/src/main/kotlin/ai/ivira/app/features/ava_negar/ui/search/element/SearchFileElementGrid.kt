package ai.ivira.app.features.ava_negar.ui.search.element

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SearchFileElementGrid(
    archiveViewProcessed: AvanegarProcessedFileView,
    onItemClick: (id: Int, title: String) -> Unit
) {
    Card(
        backgroundColor = Color_Card,
        elevation = 0.dp,
        border = if (archiveViewProcessed.isSeen) {
            BorderStroke(0.dp, Color_Card)
        } else {
            BorderStroke(0.5.dp, MaterialTheme.colors.primary)
        },
        modifier = Modifier.height(156.dp),
        onClick = {
            safeClick {
                onItemClick(archiveViewProcessed.id, archiveViewProcessed.title)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = if (archiveViewProcessed.isSeen) Color_Text_1 else MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.title
                )
            }

            Text(
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 16.dp, end = 8.dp),
                color = Color_Text_2,
                style = MaterialTheme.typography.body2,
                text = archiveViewProcessed.text
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_calendar,
                    contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
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

@ViraDarkPreview
@Composable
private fun SearchFileElementGridPreview() {
    ViraPreview {
        SearchFileElementGrid(
            archiveViewProcessed = AvanegarProcessedFileView(
                id = 0,
                title = "عنوان",
                text = "متن متن متن متن متن متن",
                createdAt = "54654",
                filePath = "SASAS",
                isSeen = true
            ),
            onItemClick = { _, _ -> }
        )
    }
}