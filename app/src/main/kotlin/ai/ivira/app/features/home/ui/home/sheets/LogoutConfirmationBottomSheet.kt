package ai.ivira.app.features.home.ui.home.sheets

import ai.ivira.app.R
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red_Opacity_15
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LogoutConfirmationBottomSheet(
    viewModel: LogoutBottomSheetViewModel,
    cancelAction: () -> Unit,
    onSuccessCallback: () -> Unit,
    onErrorCallback: (UiError) -> Unit
) {
    val uiState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

    val density = LocalDensity.current
    var loadingHeight by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiError -> onErrorCallback(uiState as UiError)
            UiSuccess -> onSuccessCallback()
            UiIdle,
            UiLoading -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_logout),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PartiallyBoldText(
            text = stringResource(id = R.string.msg_logout),
            startIndex = 15,
            endIndex = 29,
            style = MaterialTheme.typography.body2,
            highlightStyle = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier.weight(1f),
                onClick = {
                    safeClick {
                        viewModel.logout()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Primary_Opacity_15,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uiState is UiLoading) {
                    HorizontalLoadingCircles(
                        radius = 10,
                        count = 3,
                        padding = 15,
                        color = Color_Primary_300,
                        modifier = Modifier.height(with(density) { loadingHeight.toDp() })
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.lbl_exit),
                        style = MaterialTheme.typography.button
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                contentPadding = PaddingValues(14.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = {
                    safeClick {
                        viewModel.resetLogoutRequest()
                        cancelAction()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color_Red_Opacity_15,
                    contentColor = MaterialTheme.colors.onError
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_cancel),
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.onGloballyPositioned {
                        loadingHeight = it.size.height
                    }
                )
            }
        }
    }
}

@Suppress("SameParameterValue")
@Composable
private fun PartiallyBoldText(
    text: String,
    startIndex: Int,
    endIndex: Int,
    style: TextStyle,
    highlightStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = style.toSpanStyle()) {
                append(text.substring(0, startIndex))
            }
            withStyle(style = highlightStyle.toSpanStyle()) {
                append(text.substring(startIndex, endIndex))
            }
            withStyle(style = style.toSpanStyle()) {
                append(text.substring(endIndex))
            }
        },
        modifier = modifier
    )
}

@Suppress("SameParameterValue")
@Composable
private fun HorizontalLoadingCircles(
    radius: Int,
    count: Int,
    padding: Int,
    modifier: Modifier = Modifier,
    color: Color = contentColorFor(backgroundColor = LocalContentColor.current)
) {
    val transition = rememberInfiniteTransition(label = "transition")
    val offsetList = remember { mutableListOf<State<Float>>() }
    val baseDelay = 200

    if (offsetList.isEmpty()) {
        for (i in 0 until count) {
            offsetList.add(
                transition.animateFloat(
                    initialValue = 0f,
                    targetValue = radius.toFloat(),
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(count * baseDelay),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(
                            offsetMillis = i * baseDelay,
                            offsetType = StartOffsetType.FastForward
                        )
                    ),
                    label = "offset"
                )
            )
        }
    }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val length = remember { radius / 2 + (count - 1) * (padding + radius) }
        val startOffset = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(length) {
            with(density) {
                startOffset.floatValue = (maxWidth.toPx() - length) / 2
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            onDraw = {
                for (i in 0 until count) {
                    drawCircle(
                        color = color,
                        radius = offsetList[i].value,
                        center = Offset(
                            x = startOffset.floatValue + (2 * radius * i) + (padding * i),
                            y = 0f
                        )
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLogoutConfirmationBottomSheet() {
    ViraPreview {
        LogoutConfirmationBottomSheet(
            cancelAction = {},
            onErrorCallback = {},
            onSuccessCallback = {},
            viewModel = hiltViewModel()
        )
    }
}