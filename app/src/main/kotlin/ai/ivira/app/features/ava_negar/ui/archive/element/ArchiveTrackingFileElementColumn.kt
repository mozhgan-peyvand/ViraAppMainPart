package ai.ivira.app.features.ava_negar.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarTrackingFileView
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun ArchiveTrackingFileElementsColumn(
    archiveTrackingView: AvanegarTrackingFileView,
    isNetworkAvailable: Boolean,
    brush: Brush,
    onItemClick: (String) -> Unit,
    onMenuClick: (AvanegarTrackingFileView) -> Unit
) {
    Column(
        modifier = Modifier
            .height(108.dp)
            .background(brush, RoundedCornerShape(16.dp))
            .safeClickable {
                onItemClick(archiveTrackingView.token)
            }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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

            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isNetworkAvailable) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        style = MaterialTheme.typography.body2,
                        color = Color_Text_2,
                        text = stringResource(id = R.string.lbl_converting)
                    )
                } else {
                    ErrorMessage(
                        isNetworkAvailable = isNetworkAvailable,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)

                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ArchiveTrackingFileElementsColumnPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveTrackingFileElementsColumn(
                archiveTrackingView = AvanegarTrackingFileView(
                    token = "sa",
                    filePath = "Sasas",
                    title = "عنوان",
                    createdAt = "Sasasasa"
                ),
                brush = Brush.horizontalGradient(),
                isNetworkAvailable = true,
                onItemClick = {},
                onMenuClick = {}
            )
        }
    }
}