package ai.ivira.app.features.hamahang.ui.archive.element

import ai.ivira.app.R
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus.Cancel
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus.Converting
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus.Download
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus.Retry
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus.Upload
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun HamahangIconItemState(
    hamahangitemStatus: HamahangItemImageStatus,
    modifier: Modifier = Modifier,
    isInDownloadQueue: Boolean = false,
    isEnabled: Boolean = true,
    progress: Float = -1f
) {
    val icon = when (hamahangitemStatus) {
        Cancel -> R.drawable.ic_cancel
        Download -> R.drawable.ic_download_audio
        Upload -> R.drawable.ic_upload_audio
        Converting -> R.raw.circle_qualizer
        Retry -> R.drawable.ic_retry
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
                drawable = R.drawable.ic_transparent_circle,
                contentDescription = null,
                tint = if (isEnabled) Color_Primary else this,
                modifier = Modifier.fillMaxSize()
            )

            ViraIcon(
                drawable = R.drawable.ic_transparent_circle,
                contentDescription = null,
                tint = if (isEnabled) Color_Primary else this,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
            )
            if (hamahangitemStatus != Converting) {
                ViraIcon(
                    drawable = icon,
                    contentDescription = null,
                    tint = if (isEnabled) Color_White else this
                )
            } else {
                TrackingLottie(lottieFile = icon)
            }
        }
    }
}

@Composable
private fun TrackingLottie(
    modifier: Modifier = Modifier,
    lottieFile: Int
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId = lottieFile))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C21)
@Composable
private fun AudioImagePreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HamahangIconItemState(
                hamahangitemStatus = Converting,
                isEnabled = true
            )
        }
    }
}