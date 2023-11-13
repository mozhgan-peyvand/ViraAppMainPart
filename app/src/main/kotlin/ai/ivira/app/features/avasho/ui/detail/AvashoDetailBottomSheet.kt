package ai.ivira.app.features.avasho.ui.detail

import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_State_Layer_1
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp

@Composable
fun AvashoDetailBottomSheet(
    progress: Float,
    collapseToolbarAction: () -> Unit,
    modifier: Modifier = Modifier,
    avashoProcessedItem: AvashoProcessedFileView? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            CollapseStateToolbar(
                progress = progress,
                collapseToolbarAction = { collapseToolbarAction() },
                fileName = avashoProcessedItem?.fileName.orEmpty()
            )

            CollapseStatePlayer(
                fileDuration = avashoProcessedItem?.fileDuration ?: 0,
                progress = progress
            )

            Divider(modifier = Modifier.height(1.dp), color = Color_Card_Stroke)

            Text(
                text = avashoProcessedItem?.text.orEmpty(),
                style = MaterialTheme.typography.body2,
                color = Color_Text_2,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        if (progress < 1.0f && progress > 0f) {
            Surface(
                color = Color.Transparent,
                modifier = modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { },
                content = {}
            )
        }
    }
}

@Composable
private fun CollapseStateToolbar(
    progress: Float,
    modifier: Modifier = Modifier,
    collapseToolbarAction: () -> Unit,
    fileName: String
) {
    val size by remember(progress) {
        mutableStateOf(20.dp * (1 - progress))
    }

    val tintColorClose by remember(progress) {
        mutableStateOf(Color_White.copy(1 - progress))
    }

    val tintColorMenu by remember(progress) {
        mutableStateOf(Color_White.copy(progress))
    }

    val h5 = MaterialTheme.typography.h5
    val s2 = MaterialTheme.typography.subtitle2

    val textStyle by remember(progress) {
        derivedStateOf {
            lerp(h5, s2, progress)
        }
    }

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
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(48.dp * progress),
                onClick = {
                    collapseToolbarAction()
                }
            ) {
                ViraIcon(
                    drawable = drawable.ic_arrow_down,
                    contentDescription = null,
                    tint = tintColorMenu
                )
            }

            Spacer(modifier = Modifier.size(size))

            Text(
                text = fileName,
                style = textStyle,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = Color_Text_1
            )
            IconButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    safeClick {
                        // 1f means that the bottomSheet is expanded
                        if (progress == 1f) {
                            // TODO open menu bottomSheet
                            return@safeClick
                        }

                        collapseToolbarAction()
                    }
                }
            ) {
                ViraIcon(
                    drawable = drawable.ic_menu_dot_2,
                    contentDescription = null,
                    tint = Color_White.copy(progress)
                )

                ViraIcon(
                    drawable = drawable.ic_close,
                    contentDescription = null,
                    tint = tintColorClose
                )
            }
        }
    }
}

@Composable
private fun CollapseStatePlayer(
    fileDuration: Long,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val isPlaying = rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
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
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.padding(end = 24.dp),
                onClick = {
                    safeClick {
                        if (progress == 1f) {
                            // TODO move audio ten second
                            return@safeClick
                        }

                        // TODO download
                    }
                }
            ) {
                ViraImage(
                    drawable = drawable.ic_download_voice,
                    contentDescription = stringResource(id = string.lbl_download),
                    alpha = 1 - progress
                )

                ViraImage(
                    drawable = drawable.ic_ten_second_after,
                    contentDescription = stringResource(id = string.lbl_move_ten_sec_forward),
                    alpha = progress
                )
            }

            IconButton(
                onClick = {
                    safeClick {
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        shape = CircleShape,
                        color = Color_Primary
                    )
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

            IconButton(
                modifier = Modifier.padding(start = 24.dp),
                onClick = {
                    safeClick {
                        if (progress == 1f) {
                            // TODO move audio ten second
                            return@safeClick
                        }

                        // TODO share
                    }
                }
            ) {
                ViraImage(
                    drawable = drawable.ic_share_speech,
                    contentDescription = stringResource(id = string.lbl_share_file),
                    alpha = 1 - progress
                )

                ViraImage(
                    drawable = drawable.ic_ten_second_before,
                    contentDescription = stringResource(id = string.lbl_move_ten_sec_back),
                    alpha = progress
                )
            }
        }

        BottomBar(
            modifier = Modifier.height(104.dp * progress),
            onShareClick = {
                // TODO share
            },
            onSaveClick = {
                // TODO save
            }
        )
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
                onClick = {
                    safeClick {
                        onSaveClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color_Primary_300,
                    backgroundColor = Color_Primary_Opacity_15
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ViraImage(
                        drawable = drawable.ic_save,
                        contentDescription = stringResource(id = string.lbl_save),
                        modifier = modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = stringResource(id = string.lbl_save),
                        style = MaterialTheme.typography.button,
                        color = Color_Primary_300,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
                onClick = {
                    safeClick {
                        onShareClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color_Primary_300,
                    backgroundColor = Color_Primary_Opacity_15
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ViraImage(
                        drawable = drawable.ic_share,
                        contentDescription = stringResource(id = string.desc_share),
                        modifier = modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = stringResource(id = string.lbl_btn_share_text),
                        style = MaterialTheme.typography.button,
                        color = Color_Primary_300,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@ViraDarkPreview
@Composable
private fun AvashoDetailBottomSheetPreview() {
    ViraPreview {
        AvashoDetailBottomSheet(
            progress = 0f,
            collapseToolbarAction = {}
        )
    }
}