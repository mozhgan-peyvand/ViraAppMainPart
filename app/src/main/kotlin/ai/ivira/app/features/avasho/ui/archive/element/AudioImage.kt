package ai.ivira.app.features.avasho.ui.archive.element

import ai.ivira.app.R.drawable
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Cancel
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Converting
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Download
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Pause
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Play
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Retry
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Upload
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp

@Composable
fun AudioImage(
    audioImageStatus: AudioImageStatus,
    modifier: Modifier = Modifier,
    isInDownloadQueue: Boolean = false,
    isEnabled: Boolean = true,
    progress: Float = -1f
) {
    val icon = when (audioImageStatus) {
        Play -> drawable.ic_play_audio
        Pause -> drawable.ic_pause_transparent
        Cancel -> drawable.ic_cancel
        Download -> drawable.ic_download_audio
        Upload -> drawable.ic_upload_audio
        Converting -> drawable.ic_gear
        Retry -> drawable.ic_retry
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .size(48.dp)
            .clip(CircleShape)
    ) {
        if (isInDownloadQueue) {
            if (progress != -1f) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 2.dp,
                    progress = progress
                )
            } else {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 2.dp
                )
            }
        }

        with(LocalContentColor.current.copy(alpha = LocalContentAlpha.current)) {
            ViraIcon(
                drawable = drawable.ic_transparent_circle,
                contentDescription = null,
                tint = if (isEnabled) Color_Primary else this,
                modifier = Modifier.fillMaxSize()
            )

            ViraIcon(
                drawable = drawable.ic_transparent_circle,
                contentDescription = null,
                tint = if (isEnabled) Color_Primary else this,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
            )

            ViraIcon(
                drawable = icon,
                contentDescription = null,
                tint = if (isEnabled) Color_White else this
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C21)
@Composable
private fun AudioImagePreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            AudioImage(
                audioImageStatus = Converting,
                isEnabled = true
            )
        }
    }
}