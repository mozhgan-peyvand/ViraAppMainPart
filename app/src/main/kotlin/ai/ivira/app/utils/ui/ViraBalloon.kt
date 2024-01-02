package ai.ivira.app.utils.ui

import ai.ivira.app.R
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Surface_Tooltip
import ai.ivira.app.utils.ui.theme.Color_Text_Tooltip
import ai.ivira.app.utils.ui.theme.Color_Transparent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.ArrowPositionRules.ALIGN_ANCHOR
import com.skydoves.balloon.BalloonAnimation.FADE
import com.skydoves.balloon.BalloonSizeSpec.WRAP
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.BalloonWindow
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setArrowColor
import com.skydoves.balloon.overlay.BalloonOverlayRect
import com.skydoves.balloon.overlay.BalloonOverlayShape

@Composable
fun ViraBalloon(
    text: String,
    onDismiss: () -> Unit,
    marginHorizontal: Int = 0,
    marginVertical: Int = 0,
    overLayShape: BalloonOverlayShape = BalloonOverlayRect,
    arrowPosition: ArrowPositionRules = ALIGN_ANCHOR,
    content: @Composable BalloonWindow.() -> Unit
) {
    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowPositionRules(arrowPosition)
        setWidth(WRAP)
        setHeight(WRAP)
        setCornerRadius(8f)
        setMarginHorizontal(marginHorizontal)
        setMarginVertical(marginVertical)
        setBackgroundColor(Color_Transparent.toArgb())
        setBalloonAnimation(FADE)
        setArrowColor(Color_Surface_Tooltip)
        setOverlayShape(overLayShape)
        setOverlayColor(Color.Black.copy(alpha = 0.7f).toArgb())
        setIsVisibleOverlay(true)
    }

    var isClicked by remember {
        mutableStateOf(false)
    }

    Balloon(
        builder = builder,
        balloonContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var width by remember { mutableIntStateOf(-1) }

                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = Color_Text_Tooltip,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .background(Color_Surface_Tooltip, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .onGloballyPositioned {
                            width = it.size.width
                        }
                )

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(Color_Primary_Opacity_15),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        safeClick { isClicked = true }
                    },
                    modifier = Modifier.width(width.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.lbl_understood),
                        color = Color_Primary_200
                    )
                }
            }
        }
    ) { balloonWindow ->
        content(balloonWindow)

        // when button is clicked it runs
        LaunchedEffect(isClicked) {
            if (isClicked) {
                isClicked = false
                balloonWindow.dismiss()
            }
        }

        // runs when dismiss of balloonWindow is called
        balloonWindow.setOnBalloonDismissListener {
            onDismiss()
        }

        // when tooltips is showing and user click somewhere, it runs
        balloonWindow.setOnBalloonClickListener {
            balloonWindow.dismiss()
        }
    }
}