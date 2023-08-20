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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.ui.theme.Color_Card
import ir.part.app.intelligentassistant.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.ui.theme.Color_Red
import ir.part.app.intelligentassistant.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.ui.theme.Color_Text_3


@Composable
fun ArchiveProcessedFileElement(
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
        modifier = Modifier.height(156.dp),
        onClick = {
            onItemClick(archiveViewProcessed.id)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 16.dp)
                .fillMaxWidth()
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
                    color = if (archiveViewProcessed.isSeen) Color_Text_1 else MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.title
                )
                IconButton(
                    modifier = Modifier.size(48.dp),
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

@Composable
fun ArchiveTrackingFileElements(
    archiveTrackingView: AvanegarTrackingFileView,
    isNetworkAvailable: Boolean,
    onItemClick: (String) -> Unit,
    onTryAgainButtonClick: (String) -> Unit
) {
    Card(
        border = BorderStroke(0.5.dp, Color_Card_Stroke),
        modifier = Modifier.height(156.dp),
        onClick = { onItemClick(archiveTrackingView.token) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = Color_Text_1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle2,
                text = archiveTrackingView.title
            )

            if (isNetworkAvailable) {
                //TODO Remove it
                Button(
                    onClick = { onTryAgainButtonClick(archiveTrackingView.token) },
                ) {
                    Text(text = stringResource(id = R.string.lbl_try_again))
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.body2,
                    color = Color_Text_2,
                    text = stringResource(id = R.string.lbl_converting)
                )
            } else
                ArchiveBodyError(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ArchiveUploadingFileElement(
    archiveUploadingFileView: AvanegarUploadingFileView,
    isUploading: Boolean,
    isNetworkAvailable: Boolean,
    onMenuClick: (AvanegarUploadingFileView) -> Unit,
    onItemClick: (String) -> Unit
) {
    Card(
        backgroundColor = Color_Card,
        border = BorderStroke(0.5.dp, Color_Card_Stroke),
        modifier = Modifier.height(156.dp),
        onClick = { onItemClick(archiveUploadingFileView.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 16.dp)
                .fillMaxWidth()
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
                    color = Color_Text_1,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveUploadingFileView.title
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { onMenuClick(archiveUploadingFileView) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }

            if (isNetworkAvailable)
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (archiveUploadingFileView.uploadedPercent > 0 && isUploading)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {

                                Text(
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.caption,
                                    color = Color_Text_3,
                                    text = stringResource(id = R.string.lbl_uploading)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    style = MaterialTheme.typography.caption,
                                    color = Color_Text_3,
                                    text = "${(archiveUploadingFileView.uploadedPercent * 100).toInt()}%"
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(end = 8.dp),
                                progress = archiveUploadingFileView.uploadedPercent,
                            )
                        }
                    else Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.padding(bottom = 4.dp),
                            painter = painterResource(id = R.drawable.ic_in_uploading_queue),
                            contentDescription = null,
                            tint = Color_Primary_300
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            style = MaterialTheme.typography.caption,
                            color = Color_Text_3,
                            text = stringResource(id = R.string.lbl_waiting_for_upload)
                        )
                    }

                }
            else
                ArchiveBodyError(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
        }
    }
}

@Composable
private fun ArchiveBodyError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.msg_internet_connection_problem),
            style = MaterialTheme.typography.caption,
            color = Color_Red
        )
    }
}