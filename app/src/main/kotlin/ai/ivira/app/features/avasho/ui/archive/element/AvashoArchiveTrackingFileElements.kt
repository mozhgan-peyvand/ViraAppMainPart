package ai.ivira.app.features.avasho.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.DecreaseEstimateTime
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Converting
import ai.ivira.app.features.avasho.ui.archive.model.AvashoTrackingFileView
import ai.ivira.app.utils.ui.computeSecondAndMinute
import ai.ivira.app.utils.ui.computeTextBySecondAndMinute
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun AvashoArchiveTrackingFileElement(
    archiveTrackingView: AvashoTrackingFileView,
    brush: Brush,
    audioImageStatus: AudioImageStatus,
    estimateTime: () -> Double,
    onMenuClick: (AvashoTrackingFileView) -> Unit
) {
    val context = LocalContext.current

    val getNewEstimateTime = remember(archiveTrackingView.token, archiveTrackingView.lastFailure) {
        mutableIntStateOf(estimateTime().toInt())
    }

    DecreaseEstimateTime(
        estimationTime = getNewEstimateTime.intValue,
        token = archiveTrackingView.token
    ) { long ->
        getNewEstimateTime.intValue = long
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(89.dp)
            .background(brush, RoundedCornerShape(16.dp))
    ) {
        AudioImage(audioImageStatus = audioImageStatus)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color_Text_1,
                style = MaterialTheme.typography.button,
                text = archiveTrackingView.title
            )

            Text(
                color = Color_Text_2,
                style = MaterialTheme.typography.caption,
                text = stringResource(id = R.string.lbl_converting_to_audio)
            )

            if (getNewEstimateTime.intValue > 0) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        style = MaterialTheme.typography.caption,
                        color = Color_Text_2,
                        textAlign = TextAlign.Start,
                        text = stringResource(id = R.string.lbl_converting_doing)
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        style = MaterialTheme.typography.caption,
                        color = Color_Text_2,
                        textAlign = TextAlign.End,
                        text = buildString {
                            append(computeSecondAndMinute(getNewEstimateTime.intValue))
                            append(" ")
                            append(
                                computeTextBySecondAndMinute(
                                    second = getNewEstimateTime.intValue,
                                    context = context
                                )
                            )
                        }
                    )
                }
            } else {
                Text(
                    text = stringResource(
                        id = if (archiveTrackingView.processEstimation != null) {
                            R.string.lbl_wait_for_end_process
                        } else {
                            R.string.lbl_converting
                        }
                    ),
                    style = MaterialTheme.typography.caption,
                    color = Color_Text_2,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        ViraImage(
            drawable = R.drawable.ic_dots_menu,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .safeClickable {
                    onMenuClick(archiveTrackingView)
                }
        )
    }
}

@Preview
@Composable
private fun AvashoArchiveTrackingFileElementPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvashoArchiveTrackingFileElement(
                archiveTrackingView = AvashoTrackingFileView(
                    token = "sa",
                    title = "عنوان",
                    createdAt = "Sasasasa",
                    processEstimation = 0,
                    bootElapsedTime = 0L,
                    lastFailure = false
                ),
                brush = Brush.horizontalGradient(),
                audioImageStatus = Converting,
                estimateTime = { 0.0 },
                onMenuClick = {}
            )
        }
    }
}