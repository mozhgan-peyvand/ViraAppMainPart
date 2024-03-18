package ai.ivira.app.features.hamahang.ui.new_audio.components

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.ava_negar.ui.record.widgets.RecordingAnimation
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun HamahangAudioBox(
    mode: HamahangAudioBoxMode,
    playerState: VoicePlayerState,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (mode) {
            is HamahangAudioBoxMode.Preview -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(50.dp))
                    AudioFilePreview(
                        playerState = playerState,
                        onDeleteClick = onDeleteClick,
                        onProgressChanged = { playerState.seekTo(it) },
                        onPlayingChanged = { isPlaying ->
                            if (isPlaying) {
                                playerState.startPlaying()
                            } else {
                                playerState.stopPlaying()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
            HamahangAudioBoxMode.Idle,
            HamahangAudioBoxMode.Recording -> {
                val recording = mode is HamahangAudioBoxMode.Recording
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    RecordingAnimation(
                        isRecording = recording,
                        iconResWhileRecording = R.drawable.ic_stop,
                        onRecordClick = if (recording) stopRecording else startRecording,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioFilePreview(
    playerState: VoicePlayerState,
    onProgressChanged: (Float) -> Unit,
    onPlayingChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration by remember(playerState.duration) {
        mutableFloatStateOf(playerState.duration.toFloat() / 1000)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val density = LocalDensity.current

        IconButton(
            modifier = Modifier.padding(end = 16.dp),
            onClick = {
                safeClick {
                    onDeleteClick()
                }
            }
        ) {
            ViraImage(
                drawable = R.drawable.icon_trash_delete,
                contentDescription = stringResource(id = R.string.lbl_btn_delete),
                colorFilter = ColorFilter.tint(Color_Red)
            )
        }

        Text(
            text = formatDuration(playerState.elapsedTime.toLong()),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
            color = Color_Text_1,
            modifier = Modifier.widthIn(with(density) { 30.sp.toDp() })
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Slider(
                colors = SliderDefaults.colors(
                    activeTrackColor = Color_Primary_300,
                    inactiveTrackColor = Color_Surface_Container_High,
                    thumbColor = Color_White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp, start = 12.dp)
                    .weight(1f),
                value = playerState.progress,
                onValueChange = onProgressChanged,
                valueRange = 0f .. duration
            )
        }

        IconButton(
            onClick = {
                safeClick {
                    onPlayingChanged(!playerState.isPlaying)
                }
            },
            modifier = Modifier
                .size(46.dp)
                .background(
                    shape = CircleShape,
                    color = Color_Primary
                )
        ) {
            if (playerState.isPlaying) {
                ViraImage(
                    drawable = R.drawable.ic_pause,
                    contentDescription = stringResource(id = R.string.desc_stop_playing),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ViraImage(
                    drawable = R.drawable.ic_play,
                    contentDescription = stringResource(id = R.string.desc_start_playing),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

sealed class HamahangAudioBoxMode {
    data object Idle : HamahangAudioBoxMode()
    data class Preview(val file: File) : HamahangAudioBoxMode()
    data object Recording : HamahangAudioBoxMode()
}