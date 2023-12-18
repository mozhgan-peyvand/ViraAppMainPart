package ai.ivira.app.utils.ui

sealed class UiStatus

data object UiIdle : UiStatus()

data object UiLoading : UiStatus()

data class UiError(val message: String, val isSnack: Boolean = false) : UiStatus()

data object UiSuccess : UiStatus()