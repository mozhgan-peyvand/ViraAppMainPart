package ir.part.app.intelligentassistant.ui.screen.archive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.ui.theme.Card_Stroke
import ir.part.app.intelligentassistant.ui.theme.Text_1
import ir.part.app.intelligentassistant.ui.theme.Text_2
import ir.part.app.intelligentassistant.ui.theme.Text_3


@Composable
fun ArchiveProcessedFileElement(
    archiveViewProcessed: AvanegarProcessedFileView,
    onItemClick: (Int) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit
) {
    Card(
        backgroundColor = ir.part.app.intelligentassistant.ui.theme.Card,
        border = BorderStroke(
            0.5.dp,
            if (archiveViewProcessed.isSeen) Card_Stroke else MaterialTheme.colors.primary
        ),
        onClick = {
            onItemClick(archiveViewProcessed.id)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f),
                    color = if (archiveViewProcessed.isSeen) Text_1 else MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.title
                )
                IconButton(
                    onClick = { onMenuClick(archiveViewProcessed) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Text(
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                color = Text_2,
                style = MaterialTheme.typography.body2,
                text = archiveViewProcessed.text
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    color = Text_3,
                    style = MaterialTheme.typography.caption,
                    text = archiveViewProcessed.createdAt
                )
            }
        }
    }
}

@Composable
fun ArchiveTrackingFileElements(
    archiveTrackingView: AvanegarTrackingFileView,
    onItemClick: (String) -> Unit,
    onTryAgainButtonClick: (String) -> Unit
) {
    Card(
        border = BorderStroke(0.5.dp, Card_Stroke),
        onClick = { onItemClick(archiveTrackingView.token) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = Text_1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle2,
                text = archiveTrackingView.title
            )

            //TODO Remove it
            Button(
                onClick = { onTryAgainButtonClick(archiveTrackingView.token) },
            ) {
                Text(text = stringResource(id = R.string.lbl_try_again))
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.body2,
                color = Text_2,
                text = stringResource(id = R.string.lbl_converting)
            )
        }
    }
}

@Composable
fun ArchiveUploadingFileElement(
    archiveUploadingFileView: AvanegarUploadingFileView,
    onMenuClick: (AvanegarUploadingFileView) -> Unit,
    onItemClick: (String) -> Unit
) {
    Card(
        backgroundColor = ir.part.app.intelligentassistant.ui.theme.Card,
        border = BorderStroke(0.5.dp, Card_Stroke),
        onClick = { onItemClick(archiveUploadingFileView.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    color = Text_1,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveUploadingFileView.title
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { onMenuClick(archiveUploadingFileView) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.caption,
                            color = Text_3,
                            text = stringResource(
                                id = if (archiveUploadingFileView.uploadedPercent > 0) R.string.lbl_uploading
                                else R.string.lbl_waiting_for_upload
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            style = MaterialTheme.typography.caption,
                            color = Text_3,
                            text = "${(archiveUploadingFileView.uploadedPercent * 100).toInt()}%"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        progress = archiveUploadingFileView.uploadedPercent,
                    )
                }
            }
        }
    }
}
