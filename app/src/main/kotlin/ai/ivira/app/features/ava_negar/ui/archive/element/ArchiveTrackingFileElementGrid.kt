package ai.ivira.app.features.ava_negar.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.DecreaseEstimateTime
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarTrackingFileView
import ai.ivira.app.utils.ui.computeSecondAndMinute
import ai.ivira.app.utils.ui.computeTextBySecondAndMinute
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
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
import androidx.compose.ui.text.style.TextAlign.Companion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun ArchiveTrackingFileElementGrid(
    archiveTrackingView: AvanegarTrackingFileView,
    brush: Brush,
    estimateTime: () -> Double,
    onItemClick: (String) -> Unit,
    onMenuClick: (AvanegarTrackingFileView) -> Unit
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

    Column(
        modifier = Modifier
            .height(156.dp)
            .background(brush = brush, RoundedCornerShape(16.dp))
            .safeClickable {
                onItemClick(archiveTrackingView.token)
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
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    color = Color_Text_1,
                    style = MaterialTheme.typography.subtitle2,
                    text = archiveTrackingView.title
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        safeClick {
                            onMenuClick(
                                archiveTrackingView
                            )
                        }
                    }
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_dots_menu,
                        modifier = Modifier.padding(12.dp),
                        contentDescription = stringResource(id = R.string.desc_menu)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (getNewEstimateTime.intValue > 0) {
                    Text(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        style = MaterialTheme.typography.body2,
                        color = Color_Text_2,
                        textAlign = Companion.Start,
                        text = stringResource(id = R.string.lbl_converting_doing)
                    )

                    Text(
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.body2,
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
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.body2,
                        color = Color_Text_2,
                        textAlign = TextAlign.Start,
                        text = stringResource(
                            id = if (archiveTrackingView.processEstimation != null) {
                                R.string.lbl_wait_for_end_process
                            } else {
                                R.string.lbl_converting
                            }
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArchiveTrackingFileElementGridPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveTrackingFileElementGrid(
                archiveTrackingView = AvanegarTrackingFileView(
                    token = "sa",
                    filePath = "Sasas",
                    title = "عنوان",
                    createdAt = "Sasasasa",
                    processEstimation = 0,
                    lastFailure = false,
                    bootElapsedTime = 0
                ),
                brush = Brush.horizontalGradient(),
                onItemClick = {},
                onMenuClick = {},
                estimateTime = { 0.0 }
            )
        }
    }
}