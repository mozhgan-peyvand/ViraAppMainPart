package ai.ivira.app.features.hamahang.ui.new_audio.components

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.record.widgets.RecordingAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun HamahangAudioBox(
    mode: HamahangAudioBoxMode,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (mode) {
            is HamahangAudioBoxMode.Preview -> {}
            HamahangAudioBoxMode.Idle, HamahangAudioBoxMode.Recording -> {
                val recording = mode is HamahangAudioBoxMode.Recording
                RecordingAnimation(
                    isRecording = recording,
                    iconResWhileRecording = R.drawable.ic_stop,
                    onRecordClick = if (recording) stopRecording else startRecording,
                    modifier = Modifier
                        .size(170.dp)
                        .align(Alignment.Center)
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