package ai.ivira.app.utils.ui.widgets

import ai.ivira.app.utils.common.orFalse
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Cyan_200
import android.view.ViewTreeObserver
import androidx.annotation.IntRange
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun OtpCodeTextField(
    otp: String,
    isError: Boolean,
    onOtpChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    @IntRange(from = 4, to = 8) otpSize: Int
) {
    CompositionLocalProvider(LocalTextToolbar provides EmptyTextToolbar) {
        BasicTextField(
            value = TextFieldValue(otp, selection = TextRange(otp.length, otp.length)),
            onValueChange = {
                onOtpChange(it.text)
            },
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            decorationBox = {
                CompositionLocalProvider(LocalLayoutDirection provides Ltr) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    ) {
                        val itemSpaceUnit = remember { 10 }
                        val sumOfItemSpaces = remember { ((otpSize - 1) * itemSpaceUnit).dp }
                        val boxWidth = remember {
                            min((maxWidth - sumOfItemSpaces) / otpSize.toFloat(), 40.dp)
                        }

                        // Fixme: Get isFocused using interactionSource
                        var isFocused by remember { mutableStateOf(false) }
                        KeyboardVisibility(
                            onVisibilityChanged = { isKeyboardVisible ->
                                isFocused = isKeyboardVisible
                            }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(itemSpaceUnit.dp)
                        ) {
                            repeat(otpSize) { index ->
                                val char = when {
                                    index >= otp.length -> ""
                                    else -> otp[index].toString()
                                }
                                val borderColor = if (isError) {
                                    Color_Red
                                } else if (!isFocused) {
                                    Color_On_Surface_Variant
                                } else if (char.isNotBlank()) {
                                    Cyan_200
                                } else if (otp.length >= index) {
                                    Color_Primary
                                } else {
                                    Color_On_Surface_Variant
                                }

                                TextAutoSize(
                                    text = char,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.h6.copy(
                                        fontFamily = FontFamily(Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman))
                                    ),
                                    textScale = TextAutoSizeRange(
                                        min = 10.sp,
                                        max = MaterialTheme.typography.h6.fontSize
                                    ),
                                    modifier = Modifier
                                        .size(boxWidth)
                                        .border(
                                            width = 1.dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape((maxOf(16 - otpSize, 0)).dp)
                                        )
                                )
                            }
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
    }
}

@Composable
private fun KeyboardVisibility(onVisibilityChanged: (isVisible: Boolean) -> Unit) {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            val insets = ViewCompat.getRootWindowInsets(view)

            onVisibilityChanged(insets?.isVisible(WindowInsetsCompat.Type.ime()).orFalse())

            true
        }

        view.viewTreeObserver.addOnPreDrawListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
}

private val EmptyTextToolbar = object : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {}

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
    }
}