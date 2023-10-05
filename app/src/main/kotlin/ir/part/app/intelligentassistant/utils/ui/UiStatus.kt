package ir.part.app.intelligentassistant.utils.ui

sealed class UiStatus

object UiIdle : UiStatus()

object UiLoading : UiStatus()

data class UiError(val message: String) : UiStatus()

object UiSuccess : UiStatus()