package ir.part.app.intelligentassistant.ui.theme

import android.app.Activity
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColors(
    primary = Primary,
    primaryVariant = BG,
    background = BG,
    surface = Card,
    error = Red_Opacity_15,
    onPrimary = White,
    onBackground = White,
    onSurface = Text_1,
    onError = Red
)

@Composable
fun IntelligentAssistantTheme(
    content: @Composable () -> Unit
) {
    val colors = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primaryVariant.toArgb()
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            content()
        }
    }
}