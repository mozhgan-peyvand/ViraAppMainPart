package ai.ivira.app.features.avasho.ui.search

import ai.ivira.app.R
import ai.ivira.app.features.avasho.ui.archive.element.AudioImage
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Play
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AvashoSearchItemProcessedElement(
    archiveViewProcessed: AvashoProcessedFileSearchView,
    onItemClick: (Int) -> Unit
) {
    Card(
        backgroundColor = Color_Card,
        elevation = 0.dp,
        onClick = {
            safeClick {
                onItemClick(archiveViewProcessed.id)
            }
        },
        modifier = Modifier.heightIn(min = 89.dp)
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AudioImage(audioImageStatus = Play)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.fileName
                )

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        ViraIcon(
                            drawable = R.drawable.ic_calendar,
                            contentDescription = null,
                            tint = Color_Primary_300,
                            modifier = Modifier.align(alignment = CenterVertically)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            color = Color_Text_3,
                            style = MaterialTheme.typography.caption,
                            text = archiveViewProcessed.createdAt.toString()
                        )

                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
    }
}