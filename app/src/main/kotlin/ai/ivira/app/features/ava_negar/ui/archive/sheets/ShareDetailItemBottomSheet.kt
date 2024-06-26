package ai.ivira.app.features.ava_negar.ui.archive.sheets

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClickable
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ShareDetailItemBottomSheet(
    modifier: Modifier = Modifier,
    isConverting: Boolean,
    onPdfClick: () -> Unit,
    onTextClick: () -> Unit,
    onOnlyTextClick: () -> Unit,
    fileId: String? = null
) {
    val eventHandler = LocalEventHandler.current
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_loading)
    )

    if (isConverting) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .padding(vertical = 80.dp)
                    .size(100.dp)
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_share_file),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = stringResource(id = R.string.choose_format),
                    style = MaterialTheme.typography.subtitle2
                )
            }
            ShareItem(
                text = stringResource(id = R.string.lbl_share_with_Text),
                icon = R.drawable.ic_text
            ) {
                eventHandler.share(AvanegarAnalytics.shareTxt("$fileId"))
                onTextClick()
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color_OutLine
            )
            ShareItem(
                text = stringResource(id = R.string.lbl_share_with_pdf),
                icon = R.drawable.ic_pdf_new
            ) {
                eventHandler.share(AvanegarAnalytics.sharePdf("$fileId"))
                onPdfClick()
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color_OutLine
            )
            ShareItem(
                text = stringResource(id = R.string.lbl_text_without_change),
                icon = R.drawable.ic_text_new
            ) {
                eventHandler.share(AvanegarAnalytics.shareRawText("$fileId"))
                onOnlyTextClick()
            }
        }
    }
}

@Composable
private fun ShareItem(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    onShareItemClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .safeClickable {
                onShareItemClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViraIcon(
                drawable = icon,
                contentDescription = null,
                tint = Color_Text_3,
                modifier = Modifier.padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 12.dp
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_2
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun ShareDetailItemBottomSheetPreview() {
    ViraPreview {
        ShareDetailItemBottomSheet(
            isConverting = false,
            onPdfClick = {},
            onTextClick = {},
            onOnlyTextClick = {}
        )
    }
}