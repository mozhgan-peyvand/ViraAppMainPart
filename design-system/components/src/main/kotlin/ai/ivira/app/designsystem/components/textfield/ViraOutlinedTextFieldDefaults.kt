package ai.ivira.app.designsystem.components.textfield

import ai.ivira.app.designsystem.components.R
import ai.ivira.app.utils.ui.theme.Color_On_Surface
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import ai.ivira.app.utils.ui.theme.Color_Outline
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_3
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ai.ivira.app.designsystem.theme.R as ThemeR

@Immutable
object ViraOutlinedTextFieldDefaults {
    val TextFieldTextStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.button.copy(
            color = Color_On_Surface,
            fontFamily = FontFamily(
                Font(ThemeR.font.bahij_helvetica_neue_vira_edition_roman)
            )
        )

    val TextFieldShape: Shape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.medium

    val HelperTextStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.caption

    val CharCountTextStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.caption

    val DefaultErrorMessage: String
        @Composable
        get() = stringResource(R.string.acc_text_field_error)

    val ColorError = Color_Red
    val ColorHelperTextDisabled = Color_On_Surface_Variant
    val ColorHelperText = Color_Text_3

    val TextFieldMinWidth = TextFieldDefaults.MinWidth
    val TextFieldMinHeight = TextFieldDefaults.MinHeight
    val UnfocusedBorderThickness = 1.dp
    val FocusedBorderThickness = 1.dp

    @Composable
    fun TextFieldBorderBox(
        enabled: Boolean,
        isError: Boolean,
        interactionSource: MutableInteractionSource,
        colors: TextFieldColors,
        shape: Shape,
        focusedBorderThickness: Dp,
        unfocusedBorderThickness: Dp
    ) = TextFieldDefaults.BorderBox(
        enabled = enabled,
        isError = isError,
        interactionSource = interactionSource,
        colors = colors,
        shape = shape,
        focusedBorderThickness = focusedBorderThickness,
        unfocusedBorderThickness = unfocusedBorderThickness
    )

    @Composable
    fun DefaultLeadingIcon(
        @DrawableRes drawable: Int,
        modifier: Modifier = Modifier
    ) = Icon(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = modifier
    )

    @Composable
    fun DefaultPlaceholder(
        @StringRes text: Int,
        modifier: Modifier = Modifier
    ) = Text(
        text = stringResource(id = text),
        color = Color_Text_3,
        style = MaterialTheme.typography.button,
        modifier = modifier
    )

    @Composable
    fun DefaultLabel(
        @StringRes text: Int,
        modifier: Modifier = Modifier
    ) = Text(
        text = stringResource(id = text),
        color = Color_Text_3,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )

    @Composable
    fun DefaultHelperIcon(
        @DrawableRes drawable: Int,
        modifier: Modifier = Modifier
    ) = Icon(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = modifier
    )

    @Composable
    fun DefaultHelperText(
        @StringRes text: Int,
        modifier: Modifier = Modifier
    ) = Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}

@Stable
sealed interface CharacterCount {
    data object None : CharacterCount

    @Immutable
    data class Count(val maxCount: Int = UNINITIALIZED) : CharacterCount {
        init {
            require(maxCount > 0 || maxCount != UNINITIALIZED) {
                "maxCount must be greater than 0"
            }
        }
    }

    companion object {
        const val UNINITIALIZED = -1
    }
}

// TODO fix focused/unfocused color for leading/trailing icons (they are defined, but not used)
@Composable
fun TextFieldDefaults.viraTextFieldColor(

    // text color
    textColor: Color = Color_On_Surface,
    disabledTextColor: Color = Color_On_Surface_Variant,

    // label
    unfocusedLabelColor: Color = Color_Text_3,
    focusedLabelColor: Color = Color_Primary_200,
    disabledLabelColor: Color = Color_On_Surface_Variant,
    errorLabelColor: Color = Color_Red,

    // leading icon
    focusedLeadingIconColor: Color = Color_Primary_200,
    unfocusedLeadingIconColor: Color = Color_On_Surface_Variant,
    disabledLeadingIconColor: Color = Color_On_Surface_Variant,
    errorLeadingIconColor: Color = Color_Red,

    // trailingIcon
    focusedTrailingIconColor: Color = Color_Primary_200,
    unFocusedTrailingIconColor: Color = Color_On_Surface_Variant,
    disabledTrailingIconColor: Color = Color_On_Surface_Variant,
    errorTrailingIconColor: Color = Color_Red,

    // border
    focusedBorderColor: Color = Color_Primary_200,
    unfocusedBorderColor: Color = Color_Outline,
    disabledBorderColor: Color = Color_On_Surface_Variant,
    errorBorderColor: Color = Color_Red,

    // cursor
    cursorColor: Color = Color_Primary_200,
    errorCursorColor: Color = Color_Red,

    // placeHolder
    placeholderColor: Color = Color_On_Surface,
    disabledPlaceholderColor: Color = Color_On_Surface_Variant,

    backgroundColor: Color = Color.Transparent
) = outlinedTextFieldColors(
    // text color
    textColor = textColor,
    disabledTextColor = disabledTextColor,

    // label
    focusedLabelColor = focusedLabelColor,
    unfocusedLabelColor = unfocusedLabelColor,
    disabledLabelColor = disabledLabelColor,
    errorLabelColor = errorLabelColor,

    // leading icon
    leadingIconColor = unfocusedLeadingIconColor,
    disabledLeadingIconColor = disabledLeadingIconColor,
    errorLeadingIconColor = errorLeadingIconColor,

    // trailingIcon
    trailingIconColor = unFocusedTrailingIconColor,
    disabledTrailingIconColor = disabledTrailingIconColor,
    errorTrailingIconColor = errorTrailingIconColor,

    // border color
    focusedBorderColor = focusedBorderColor,
    unfocusedBorderColor = unfocusedBorderColor,
    disabledBorderColor = disabledBorderColor,
    errorBorderColor = errorBorderColor,

    // cursor
    cursorColor = cursorColor,
    errorCursorColor = errorCursorColor,

    // placeHolder
    placeholderColor = placeholderColor,
    disabledPlaceholderColor = disabledPlaceholderColor,

    backgroundColor = backgroundColor
)