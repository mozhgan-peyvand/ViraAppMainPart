package ai.ivira.app.utils.ui.preview

import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.analytics.MockEventHandler
import ai.ivira.app.utils.ui.theme.ViraTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun ViraPreview(content: @Composable () -> Unit) {
    ViraTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalEventHandler provides MockEventHandler()
        ) {
            content()
        }
    }
}