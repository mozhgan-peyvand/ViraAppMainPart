package ai.ivira.app.features.avasho.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Retry
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Upload
import ai.ivira.app.features.avasho.ui.archive.model.AvashoUploadingFileView
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun AvashoArchiveUploadingFileElement(
    avashoUploadingFileView: AvashoUploadingFileView,
    isNetworkAvailable: Boolean,
    isErrorState: Boolean,
    onTryAgainClick: (AvashoUploadingFileView) -> Unit,
    onMenuClick: (AvashoUploadingFileView) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color_Card, RoundedCornerShape(16.dp))
            .height(89.dp)
            .fillMaxWidth()
    ) {
        IconButton(
            enabled = isNetworkAvailable,
            onClick = {
                safeClick {
                    if (isErrorState) onTryAgainClick(avashoUploadingFileView)
                }
            }
        ) {
            AudioImage(
                audioImageStatus = if (!isErrorState) Upload else Retry,
                isEnabled = isNetworkAvailable
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color_Text_1,
                style = MaterialTheme.typography.subtitle2,
                text = avashoUploadingFileView.title
            )

            if (isErrorState) {
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = stringResource(id = R.string.msg_internet_connection_problem),
                    style = MaterialTheme.typography.caption,
                    color = Color_Red
                )
            } else {
                if (isNetworkAvailable) {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color_Text_2,
                        style = MaterialTheme.typography.caption,
                        text = stringResource(id = R.string.lbl_uploading)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    LinearProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(end = 8.dp)
                    )
                } else {
                    Text(
                        text = stringResource(
                            id = R.string.msg_upload_will_start_after_connect_to_internet
                        ),
                        style = MaterialTheme.typography.caption,
                        color = Color_Red
                    )
                }
            }
        }

        ViraImage(
            drawable = R.drawable.ic_dots_menu,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .safeClickable {
                    onMenuClick(avashoUploadingFileView)
                }
        )
    }
}

@Preview
@Composable
private fun AvashoArchiveUploadingFileElementPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvashoArchiveUploadingFileElement(
                avashoUploadingFileView = AvashoUploadingFileView(
                    id = "id",
                    title = "عنوان",
                    createdAt = 5456465L,
                    speaker = "",
                    text = "asasas"
                ),
                isNetworkAvailable = true,
                isErrorState = false,
                onTryAgainClick = {},
                onMenuClick = {}
            )
        }
    }
}