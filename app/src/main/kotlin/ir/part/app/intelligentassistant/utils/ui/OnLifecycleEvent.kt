package ir.part.app.intelligentassistant.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun OnLifecycleEvent(
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val startHandler by rememberUpdatedState(onStart)
    val resumeHandler by rememberUpdatedState(onResume)
    val stopHandler by rememberUpdatedState(onStop)
    val pauseHandler by rememberUpdatedState(onPause)

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startHandler()
                Lifecycle.Event.ON_RESUME -> resumeHandler()
                Lifecycle.Event.ON_PAUSE -> pauseHandler()
                Lifecycle.Event.ON_STOP -> stopHandler()
                Lifecycle.Event.ON_ANY,
                Lifecycle.Event.ON_CREATE,
                Lifecycle.Event.ON_DESTROY -> {
                }
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}