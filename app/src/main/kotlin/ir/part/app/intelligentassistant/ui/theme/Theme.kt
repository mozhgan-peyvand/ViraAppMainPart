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
    primary = Color_Primary,
    primaryVariant = Color_BG,
    background = Color_BG,
    surface = Color_Card,
    error = Color_Red_Opacity_15,
    onPrimary = Color_White,
    onBackground = Color_White,
    onSurface = Color_Text_1,
    onError = Color_Red
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