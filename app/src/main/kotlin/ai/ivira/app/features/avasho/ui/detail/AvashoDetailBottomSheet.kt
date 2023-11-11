package ai.ivira.app.features.avasho.ui.detail

import ai.ivira.app.R
import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_State_Layer_1
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp

@Composable
fun AvashoDetailBottomSheet(
    modifier: Modifier,
    collapseToolbarAction: () -> Unit,
    avashoProcessedItem: AvashoProcessedFileView? = null
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CollapseStateToolbar(
            collapseToolbarAction = { collapseToolbarAction() },
            fileName = avashoProcessedItem?.fileName ?: ""
        )
        CollapseStatePlayer(fileDuration = avashoProcessedItem?.fileDuration ?: 0)
        Divider(modifier = Modifier.height(1.dp), color = Color_Card_Stroke)
        Text(
            text = avashoProcessedItem?.text ?: "",
            style = MaterialTheme.typography.body2,
            color = Color_Text_2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun CollapseStateToolbar(
    modifier: Modifier = Modifier,
    collapseToolbarAction: () -> Unit,
    fileName: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Divider(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(5.dp)
                .width(42.dp)
                .align(CenterHorizontally)
                .clip(shape = RoundedCornerShape(4.dp)),
            color = Color_State_Layer_1
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 28.dp, end = 8.dp),
                color = Color_Text_1
            )
            IconButton(
                onClick = {
                    safeClick {
                        collapseToolbarAction()
                    }
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_close,
                    contentDescription = null,
                    tint = Color_White,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CollapseStatePlayer(
    modifier: Modifier = Modifier,
    fileDuration: Long
) {
    val isPlaying = rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = formatDuration(fileDuration),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                color = Color_Text_3
            )
            Text(
                text = formatDuration(0),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(1f),
                color = Color_Text_3
            )
        }
        CompositionLocalProvider(LocalLayoutDirection provides Ltr) {
            Slider(
                value = 0f,
                onValueChange = {
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = Color_Primary_300,
                    inactiveTrackColor = Color_Surface_Container_High,
                    thumbColor = Color_White
                )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement =
            Arrangement.Center
        ) {
            ViraImage(
                drawable = R.drawable.ic_download_voice,
                contentDescription = stringResource(id = R.string.lbl_download),
                modifier = Modifier.padding(end = 24.dp)

            )
            IconButton(
                onClick = {
                    safeClick {
                    }
                },
                modifier = Modifier.size(46.dp)
            ) {
                if (isPlaying.value) {
                    ViraImage(
                        drawable = drawable.ic_pause,
                        contentDescription = stringResource(id = string.desc_stop_playing),
                        modifier = modifier.fillMaxSize()
                    )
                } else {
                    ViraImage(
                        drawable = drawable.ic_play,
                        contentDescription = stringResource(id = string.desc_start_playing),
                        modifier = modifier.fillMaxSize()
                    )
                }
            }
            ViraImage(
                drawable = R.drawable.ic_share_speech,
                contentDescription = stringResource(id = R.string.lbl_share_file),
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101112)
@Composable
private fun AvashoDetailBottomSheetPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AvashoDetailBottomSheet(modifier = Modifier, collapseToolbarAction = {})
    }
}