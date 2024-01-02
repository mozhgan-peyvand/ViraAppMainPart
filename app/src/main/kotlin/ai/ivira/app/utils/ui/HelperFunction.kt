package ai.ivira.app.utils.ui

import androidx.compose.material.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.RoundingMode

fun showMessage(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message = message)
    }
}

fun convertByteToMB(size: Double): String {
    if (size <= 0) {
        return "0"
    }

    return ((size / 1024) / 1024)
        .toBigDecimal()
        .setScale(1, RoundingMode.UP)
        .toDouble()
        .toString()
}