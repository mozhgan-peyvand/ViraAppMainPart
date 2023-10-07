package ai.ivira.app.utils.data

import ai.ivira.app.utils.data.NetworkStatus.Available
import ai.ivira.app.utils.data.NetworkStatus.Unavailable
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NetworkStatusTracker @Inject constructor(
    @ApplicationContext context: Context,
    networkHandler: NetworkHandler
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(Unavailable)
            }

            override fun onAvailable(network: Network) {
                trySend(Available)
            }

            override fun onLost(network: Network) {
                trySend(Unavailable)
            }
        }

        trySend(if (networkHandler.hasNetworkConnection()) Available else Unavailable)

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }
}