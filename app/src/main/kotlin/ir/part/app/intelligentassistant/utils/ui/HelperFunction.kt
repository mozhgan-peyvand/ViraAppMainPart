package ir.part.app.intelligentassistant.utils.ui

import androidx.compose.material.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun showMessage(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message = message)
    }
}