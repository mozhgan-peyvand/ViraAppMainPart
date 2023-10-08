package ai.ivira.app.utils.ui

sealed class UiStatus

object UiIdle : UiStatus()

object UiLoading : UiStatus()

data class UiError(val message: String, val isSnack: Boolean = false) : UiStatus()

object UiSuccess : UiStatus()