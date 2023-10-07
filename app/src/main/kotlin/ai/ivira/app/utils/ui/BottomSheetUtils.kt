@file:Suppress("NOTHING_TO_INLINE")

package ai.ivira.app.utils.ui

import androidx.compose.material.ModalBottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// TODO: remove if found annoying!
inline fun ModalBottomSheetState.hide(scope: CoroutineScope) {
    scope.launch {
        hide()
    }
}

inline fun ModalBottomSheetState.show(scope: CoroutineScope) {
    scope.launch {
        show()
    }
}

inline fun ModalBottomSheetState.hideAndShow(scope: CoroutineScope) {
    scope.launch {
        if (isVisible) {
            hide()
        }
        show()
    }
}