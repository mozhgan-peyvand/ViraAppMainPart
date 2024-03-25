package ai.ivira.app.features.hamahang.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangSpeakerView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.convertByteToMB
import ai.ivira.app.utils.ui.millisecondsToTime
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun HamahangArchiveProcessedFileElement(
    archiveViewProcessed: HamahangProcessedFileView,
    isNetworkAvailable: Boolean,
    isDownloadFailure: Boolean,
    isInDownloadQueue: Boolean,
    onItemClick: (HamahangProcessedFileView) -> Unit,
    onMenuClick: (HamahangProcessedFileView) -> Unit
) {
    val isDownloaded = File(archiveViewProcessed.filePath).exists()

    Card(
        backgroundColor = Color_Card,
        elevation = 0.dp,
        border = if (archiveViewProcessed.isSeen) {
            BorderStroke(0.dp, Color_Card)
        } else {
            BorderStroke(0.5.dp, MaterialTheme.colors.primary)
        },
        modifier = Modifier.heightIn(min = 89.dp),
        onClick = {
            safeClick {
                onItemClick(archiveViewProcessed)
            }
        }
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HamahangIconItemState(
                hamahangitemStatus = if (isInDownloadQueue) {
                    HamahangItemImageStatus.Cancel
                } else if (isDownloadFailure) {
                    HamahangItemImageStatus.Retry
                } else {
                    HamahangItemImageStatus.Download
                },
                isInDownloadQueue = isInDownloadQueue,
                progress = archiveViewProcessed.downloadingPercent
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveViewProcessed.title,
                    color = if (archiveViewProcessed.isSeen) {
                        Color_Text_1
                    } else {
                        MaterialTheme.colors.primary
                    }
                )

                if (!isNetworkAvailable && isDownloadFailure) {
                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = stringResource(id = R.string.msg_internet_connection_problem),
                        style = MaterialTheme.typography.caption,
                        color = Color_Red
                    )
                } else {
                    if (!isDownloaded) {
                        if (
                            archiveViewProcessed.downloadingPercent != 0f &&
                            archiveViewProcessed.downloadingPercent != -1f
                        ) {
                            Text(
                                // fixme set this correctly
                                text = buildString {
                                    append(stringResource(id = R.string.lbl_downloading_file))
                                    append(" ")
                                    append("(")
                                    append(
                                        convertByteToMB(
                                            archiveViewProcessed.downloadedBytes?.toDouble()
                                                .orZero()
                                        )
                                    )
                                    append("/")

                                    append(
                                        convertByteToMB(
                                            archiveViewProcessed.fileSize?.toDouble().orZero()
                                        )
                                    )
                                    append(stringResource(id = R.string.lbl_mb))
                                    append(")")
                                },
                                style = MaterialTheme.typography.caption,
                                color = Color_Text_2
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.lbl_ready_for_download),
                                style = MaterialTheme.typography.caption,
                                color = Color_Text_2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isDownloaded) {
                            Text(
                                color = Color_Text_2,
                                style = MaterialTheme.typography.caption,
                                text = buildString {
                                    append(
                                        convertByteToMB(
                                            archiveViewProcessed.fileSize?.toDouble().orZero()
                                        )
                                    )
                                    append(stringResource(id = R.string.lbl_mb))
                                }
                            )
                        }

                        if (isInDownloadQueue || isDownloaded) {
                            Row {
                                ViraIcon(
                                    drawable = R.drawable.ic_time,
                                    contentDescription = null,
                                    modifier = Modifier.align(alignment = CenterVertically),
                                    tint = Color_Primary_300
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    color = Color_Text_3,
                                    style = MaterialTheme.typography.caption,
                                    text = millisecondsToTime(archiveViewProcessed.fileDuration)
                                )
                            }
                        }

                        Row {
                            ViraIcon(
                                drawable = R.drawable.ic_calendar,
                                contentDescription = null,
                                modifier = Modifier.align(alignment = CenterVertically),
                                tint = Color_Primary_300
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                color = Color_Text_3,
                                style = MaterialTheme.typography.caption,
                                text = archiveViewProcessed.createdAt
                            )

                            Spacer(modifier = Modifier.size(8.dp))
                        }

                        // fixme remove it after menu implemented
                        Spacer(modifier = Modifier.size(4.dp))
                    }
                }
            }
            ViraImage(
                drawable = R.drawable.ic_dots_menu,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .safeClickable {
                        onMenuClick(archiveViewProcessed)
                    }
            )
        }
    }
}

@Preview
@Composable
private fun HamahangArchiveProcessedFileElementPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            HamahangArchiveProcessedFileElement(
                archiveViewProcessed = HamahangProcessedFileView(
                    id = 0,
                    title = "عنوان",
                    createdAt = "54654",
                    fileUrl = "SASAS",
                    filePath = "aaa",
                    fileSize = 0,
                    downloadingPercent = 0.5f,
                    downloadedBytes = 1055205252558,
                    fileDuration = 0L,
                    speaker = HamahangSpeakerView.Chavoshi,
                    isSeen = false, inputFilePath = ""
                ),
                isNetworkAvailable = true,
                isDownloadFailure = false,
                isInDownloadQueue = true,
                onItemClick = {},
                onMenuClick = {}
            )
        }
    }
}