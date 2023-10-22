package ai.ivira.app.utils.ui.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalEventHandler = staticCompositionLocalOf<EventHandler> {
    error("no eventHandler provided")
}