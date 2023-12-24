package ai.ivira.app.utils.ui.widgets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.theme.Color_Info
import ai.ivira.app.utils.ui.theme.Color_Info_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Red_800
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ViraBannerWithAnimation(
    isVisible: Boolean,
    bannerInfo: ViraBannerInfo,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(animationSpec = tween(500)),
        exit = fadeOut() + shrinkVertically(animationSpec = tween(500))
    ) {
        ViraBanner(
            bannerInfo = bannerInfo,
            modifier = modifier
        )
    }
}

@Composable
private fun ViraBanner(
    bannerInfo: ViraBannerInfo,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(bannerInfo.bgColor)
            .padding(8.dp)
    ) {
        ViraIcon(
            drawable = bannerInfo.iconRes,
            contentDescription = null,
            tint = bannerInfo.tint
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = bannerInfo.message,
            style = MaterialTheme.typography.body2,
            color = bannerInfo.tint
        )
    }
}

@Stable
sealed class ViraBannerInfo(
    @DrawableRes val iconRes: Int,
    val message: String,
    val bgColor: Color,
    val tint: Color
) {

    @Stable
    class Error(
        message: String,
        iconRes: Int
    ) : ViraBannerInfo(
        iconRes = iconRes,
        message = message,
        bgColor = Color_Red_800,
        tint = Color_Red
    )

    @Stable
    class Warning(
        message: String,
        iconRes: Int
    ) : ViraBannerInfo(
        iconRes = iconRes,
        message = message,
        bgColor = Color_Info_Opacity_15,
        tint = Color_Info
    )
}

@ViraDarkPreview
@Preview
@Composable
fun PreviewViraBanner() {
    ViraPreview {
        ViraBanner(
            bannerInfo = ViraBannerInfo.Warning(
                message = stringResource(id = R.string.msg_vpn_is_connected_error),
                iconRes = R.drawable.ic_warning_vpn
            )
        )
    }
}