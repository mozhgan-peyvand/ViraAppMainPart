package ai.ivira.app.utils.data

sealed class NetworkStatus {
    data class Available(val hasVpn: Boolean) : NetworkStatus()
    data object Unavailable : NetworkStatus()
}