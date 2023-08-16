package ir.part.app.intelligentassistant.utils.data

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}