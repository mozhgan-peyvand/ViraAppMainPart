package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.ui.widgets.ViraIcon


@Composable
fun ArchiveUploadingFileElementColumn(
    archiveUploadingFileView: AvanegarUploadingFileView,
    isUploading: Boolean,
    isNetworkAvailable: Boolean,
    isErrorState: Boolean,
    onTryAgainClick: (AvanegarUploadingFileView) -> Unit,
    onMenuClick: (AvanegarUploadingFileView) -> Unit,
    onItemClick: (AvanegarUploadingFileView) -> Unit
) {
    Card(backgroundColor = Color_Card,
        elevation = 0.dp,
        modifier = Modifier.height(108.dp),
        onClick = {
            safeClick {
                onItemClick(archiveUploadingFileView)
            }
        }) {
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    bottom = if (!isErrorState) 16.dp else 8.dp
                )
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

                IconButton(onClick = {
                    safeClick {
                        onMenuClick(
                            archiveUploadingFileView
                        )
                    }
                }) {
                    ViraIcon(
                        drawable = R.drawable.ic_dots_menu,
                        modifier = Modifier.padding(12.dp),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }

            if (!isErrorState && isNetworkAvailable)
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
                        ViraIcon(
                            drawable = R.drawable.ic_in_uploading_queue,
                            contentDescription = null,
                            modifier = Modifier.padding(bottom = 4.dp),
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
                ArchiveBodyErrorColumn(
                    isNetworkAvailable = isNetworkAvailable,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onTryAgainClick = { onTryAgainClick(archiveUploadingFileView) }
                )
        }
    }
}

@Preview
@Composable
private fun ArchiveUploadingFileElementColumn() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveUploadingFileElementColumn(
                archiveUploadingFileView = AvanegarUploadingFileView(
                    id = "id",
                    title = "عنوان",
                    filePath = "filePath",
                    createdAt = 5456465L,
                    uploadedPercent = 30f,
                    fileDuration = 0,
                    isUploadingFinished = false,
                ),
                isUploading = false,
                isNetworkAvailable = true,
                isErrorState = false,
                onTryAgainClick = {},
                onMenuClick = {},
                onItemClick = {},
            )
        }
    }
}


