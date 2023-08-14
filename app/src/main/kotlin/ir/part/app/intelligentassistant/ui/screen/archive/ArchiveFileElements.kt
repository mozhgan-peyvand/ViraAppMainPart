package ir.part.app.intelligentassistant.ui.screen.archive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarUploadingFileView


@Composable
fun ArchiveProcessedFileElement(
    archiveViewProcessed: AvanegarProcessedFileView,
    onItemClick: (Int) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit
) {
    Card(
        backgroundColor = if (archiveViewProcessed.isSeen) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        onClick = { onItemClick(archiveViewProcessed.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    text = archiveViewProcessed.title
                )
                IconButton(
                    onClick = {
                        onMenuClick(archiveViewProcessed)
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = archiveViewProcessed.text
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = archiveViewProcessed.createdAt
            )
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
        backgroundColor = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(16.dp),
        onClick = { onItemClick(archiveTrackingView.token) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = archiveTrackingView.title
            )

            //TODO Remove it
            Button(
                onClick = { onTryAgainButtonClick(archiveTrackingView.token) },
            ) {
                Text(text = stringResource(id = R.string.lbl_try_again))
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
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
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        onClick = { onItemClick(archiveUploadingFileView.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    text = archiveUploadingFileView.title
                )
                IconButton(
                    onClick = {
                        onMenuClick(archiveUploadingFileView)
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ProgressBarSection(archiveUploadingFileView)
            }
        }
    }
}

@Composable
private fun ProgressBarSection(
    archiveUploadingFileView: AvanegarUploadingFileView
) {

    if (archiveUploadingFileView.uploadedPercent > 0) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.lbl_uploading)
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    fontSize = 8.sp,
                    text = "${(archiveUploadingFileView.uploadedPercent * 100).toInt()}%"
                )
            }

            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(13.dp),
                progress = archiveUploadingFileView.uploadedPercent,
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.lbl_waiting_for_upload)
                )
            }

            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(13.dp)
            )
        }
    }

}