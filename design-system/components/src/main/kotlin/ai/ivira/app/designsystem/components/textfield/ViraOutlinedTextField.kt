package ai.ivira.app.designsystem.components.textfield

import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.CharCountTextStyle
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.ColorError
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.ColorHelperText
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.ColorHelperTextDisabled
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.FocusedBorderThickness
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.HelperTextStyle
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.TextFieldBorderBox
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.TextFieldMinHeight
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.TextFieldMinWidth
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.TextFieldShape
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.TextFieldTextStyle
import ai.ivira.app.designsystem.components.textfield.ViraOutlinedTextFieldDefaults.UnfocusedBorderThickness
import ai.ivira.app.designsystem.components.textfield.internal.Decoration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.TextFieldDecorator
import androidx.compose.foundation.text2.input.CodepointTransformation
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.maxLengthInChars
import androidx.compose.foundation.text2.input.then
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun ViraOutlinedTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextFieldTextStyle,
    cursorBrush: Brush? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    interactionSource: MutableInteractionSource? = null,
    codepointTransformation: CodepointTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
    isError: Boolean = false,
    characterCount: CharacterCount = CharacterCount.None,
    colors: TextFieldColors = TextFieldDefaults.viraTextFieldColor(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = TextFieldShape,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    helperText: @Composable (() -> Unit)? = null,
    helperIcon: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val isKeyBoardVisible = WindowInsets.isImeVisible

    LaunchedEffect(isKeyBoardVisible) {
        if (!isKeyBoardVisible) {
            focusManager.clearFocus()
        }
    }

    val inputTransform = when (characterCount) {
        is CharacterCount.None -> inputTransformation
        is CharacterCount.Count -> {
            inputTransformation?.then(
                InputTransformation.maxLengthInChars(characterCount.maxCount)
            ) ?: InputTransformation.maxLengthInChars(characterCount.maxCount)
        }
    }

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val defaultDecorator = TextFieldDecorator { innerTextField ->
        TextFieldDefaults.OutlinedTextFieldDecorationBox(
            value = state.text.toString(),
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = lineLimits is TextFieldLineLimits.SingleLine,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            border = {
                TextFieldBorderBox(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = shape,
                    focusedBorderThickness = FocusedBorderThickness,
                    unfocusedBorderThickness = UnfocusedBorderThickness
                )
            }
        )
    }

    Column(modifier = modifier) {
        BasicTextField2(
            state = state,
            enabled = enabled,
            readOnly = readOnly,
            cursorBrush = cursorBrush ?: SolidColor(colors.cursorColor(isError).value),
            inputTransformation = inputTransform,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            lineLimits = lineLimits,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            codepointTransformation = codepointTransformation,
            decorator = defaultDecorator,
            scrollState = scrollState,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.backgroundColor(enabled).value, shape)
                .defaultMinSize(
                    minWidth = TextFieldMinWidth,
                    minHeight = TextFieldMinHeight
                )
        )

        if (helperIcon != null || helperText != null || characterCount is CharacterCount.Count) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .heightIn(max = 16.dp)
            ) {
                val textAndIconColor = remember(isError, enabled) {
                    if (isError) {
                        ColorError
                    } else if (!enabled) {
                        ColorHelperTextDisabled
                    } else {
                        ColorHelperText
                    }
                }

                helperIcon?.let {
                    Decoration(
                        contentColor = textAndIconColor,
                        content = it
                    )
                }

                Spacer(modifier = Modifier.size(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    helperText?.let {
                        Decoration(
                            contentColor = textAndIconColor,
                            typography = HelperTextStyle,
                            content = it
                        )
                    }
                }

                if (characterCount is CharacterCount.Count) {
                    if (characterCount.maxCount != CharacterCount.UNINITIALIZED) {
                        Text(
                            text = "${characterCount.maxCount}/",
                            style = CharCountTextStyle,
                            maxLines = 1,
                            color = textAndIconColor
                        )
                    }

                    Text(
                        text = state.text.length.toString(),
                        style = CharCountTextStyle,
                        maxLines = 1,
                        color = textAndIconColor
                    )
                }
            }
        }
    }
}