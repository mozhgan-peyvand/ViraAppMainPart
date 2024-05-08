package ai.ivira.app.features.config.data

import ai.ivira.app.features.config.data.ViraConfigs.Hamahang
import ai.ivira.app.features.config.data.ViraConfigs.Tiles
import ai.ivira.app.features.config.data.ViraConfigs.Versions
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppException
import ai.ivira.app.utils.data.api_result.AppResult
import android.text.format.DateUtils
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigManager @Inject constructor(
    private val configRepository: ConfigRepository,
    statusTracker: NetworkStatusTracker
) {
    private val coroutineScope = ProcessLifecycleOwner.get().lifecycleScope
    private val networkStatus = statusTracker.networkStatus.stateIn(
        coroutineScope, SharingStarted.WhileSubscribed(5000),
        NetworkStatus.Unavailable
    )

    init {
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                yield() // we call this to make sure this scope is still active
                when (val result = fetchConfigService()) {
                    ConfigResult.FailNoNetwork -> {
                        log("RESULT: Failed Because there is no network, so waiting for network to be active")
                        // we wait for network to connect the immediately try again
                        networkStatus.first { it is NetworkStatus.Available }
                    }
                    is ConfigResult.NotYetTime -> {
                        log("RESULT: Need to wait more: ${result.left.coerceAtLeast(500)}ms")
                        delay(result.left.coerceAtLeast(500))
                    }
                    ConfigResult.Fail,
                    ConfigResult.Success -> {
                        log("RESULT: $result, waiting ${CONFIG_DELAY}ms")
                        delay(CONFIG_DELAY)
                    }
                }
                log("-----------------")
            }
        }
    }

    private suspend fun fetchConfigService(): ConfigResult {
        val now = System.currentTimeMillis()
        val lastUpdateFetchTime = configRepository.getLastUpdateFetchTime()
        val lastUpdate = configRepository.getLastUpdate()

        // If this is true, means we either do not have data, ot corrupt data
        if (lastUpdateFetchTime == null || lastUpdate.isEmpty() || now < lastUpdateFetchTime) {
            log("SERVICE: Either there is no data, or the current data is invalid")
            log("\tlastUpdateFetchTime=[$lastUpdateFetchTime]")
            log("\tlastUpdate=[$lastUpdate]")
            log("\tisTimeOk=[${now < (lastUpdateFetchTime ?: 0)}]")
            return fetchAllConfigs()
        }

        // reaching here means data is valid, we just need to make sure enough time has
        // passed from the previous request!
        val timeDiff = now - lastUpdateFetchTime
        if (timeDiff < CONFIG_DELAY) {
            log("SERVICE: Need to wait for ${CONFIG_DELAY - timeDiff}ms")
            return ConfigResult.NotYetTime(CONFIG_DELAY - timeDiff)
        }

        // enough time has passed so we fetch lastUpdate to see what has changed!
        log("SERVICE: Fetching lastUpdate")
        return when (val result = configRepository.fetchLastUpdate()) {
            is AppResult.Error -> {
                log("REQUEST: Fetching lastUpdate failed(${result.error}")
                if (result.error is AppException.NetworkConnectionException) {
                    ConfigResult.FailNoNetwork
                } else {
                    ConfigResult.Fail
                }
            }
            is AppResult.Success -> {
                log("REQUEST: Fetching lastUpdate succeeded")
                processLastUpdate(
                    newLastUpdate = result.data,
                    previousLastUpdate = lastUpdate
                )
            }
        }
    }

    private suspend fun fetchAllConfigs(): ConfigResult {
        return when (val result = configRepository.fetchAllConfigs()) {
            is AppResult.Error -> {
                log("REQUEST: Fetching allConfigs failed(${result.error}")
                if (result.error is AppException.NetworkConnectionException) {
                    ConfigResult.FailNoNetwork
                } else {
                    ConfigResult.Fail
                }
            }
            is AppResult.Success -> {
                log("REQUEST: Fetching allConfigs succeeded")
                configRepository.updateLastUpdateFetchTime()
                ConfigResult.Success
            }
        }
    }

    private suspend fun processLastUpdate(
        newLastUpdate: Map<String, Long>,
        previousLastUpdate: Map<String, Long>
    ): ConfigResult {
        val changed = compareLastUpdates(newLastUpdate, previousLastUpdate)
        if (changed.size > 1) {
            log("SERVICE: More than one config changed: [$changed]")
            return fetchAllConfigs().also { result ->
                if (result == ConfigResult.Success) {
                    configRepository.insertLastUpdate(newLastUpdate)
                }
            }
        } else if (changed.size == 1) {
            log("SERVICE: Only one config changed: ${changed.first()}")
            when (changed.first()) {
                Tiles.value -> {
                    log("SERVICE: Fetching tiles...")
                    return fetchTiles(newLastUpdate)
                }
                Versions.value -> {
                    log("SERVICE: Fetching versions...")
                    return fetchVersions(newLastUpdate)
                }
                Hamahang.value -> {
                    log("SERVICE: Fetching hamahang...")
                    return fetchHamahang(newLastUpdate)
                }
            }
            // fetch that specific response
        }

        log("SERVICE: Looks like there is nothing to do")
        // coming here mean either no config changed, or some unknown one changed (
        // which this version does not care )
        configRepository.updateLastUpdateFetchTime()
        return ConfigResult.Success
    }

    private fun compareLastUpdates(
        newLastUpdate: Map<String, Long>,
        previousLastUpdate: Map<String, Long>
    ): Set<String> {
        return buildSet {
            val supportedConfigs = ViraConfigs.entries.map { it.value }
            newLastUpdate.filterKeys { config -> config in supportedConfigs }
                .forEach { (config, timestamp) ->
                    if (timestamp > previousLastUpdate[config].orZero()) {
                        add(config)
                    }
                }
        }
    }

    private suspend fun fetchTiles(newLastUpdate: Map<String, Long>): ConfigResult {
        return when (val result = configRepository.fetchTiles()) {
            is AppResult.Error -> {
                log("REQUEST: Fetching tiles failed(${result.error}")
                if (result.error is AppException.NetworkConnectionException) {
                    ConfigResult.FailNoNetwork
                } else {
                    ConfigResult.Fail
                }
            }
            is AppResult.Success -> {
                log("REQUEST: Fetching tiles succeeded")
                configRepository.updateLastUpdateFetchTime()
                configRepository.insertLastUpdate(newLastUpdate)
                ConfigResult.Success
            }
        }
    }

    private suspend fun fetchVersions(newLastUpdate: Map<String, Long>): ConfigResult {
        return when (val result = configRepository.fetchVersions()) {
            is AppResult.Error -> {
                log("REQUEST: Fetching versions failed(${result.error}")
                if (result.error is AppException.NetworkConnectionException) {
                    ConfigResult.FailNoNetwork
                } else {
                    ConfigResult.Fail
                }
            }
            is AppResult.Success -> {
                log("REQUEST: Fetching versions succeeded")
                configRepository.updateLastUpdateFetchTime()
                configRepository.insertLastUpdate(newLastUpdate)
                ConfigResult.Success
            }
        }
    }

    private suspend fun fetchHamahang(newLastUpdate: Map<String, Long>): ConfigResult {
        return when (val result = configRepository.fetchHamahang()) {
            is AppResult.Error -> {
                log("REQUEST: Fetching hamahang failed(${result.error}")
                if (result.error is AppException.NetworkConnectionException) {
                    ConfigResult.FailNoNetwork
                } else {
                    ConfigResult.Fail
                }
            }
            is AppResult.Success -> {
                log("REQUEST: Fetching hamahang succeeded")
                configRepository.updateLastUpdateFetchTime()
                configRepository.insertLastUpdate(newLastUpdate)
                ConfigResult.Success
            }
        }
    }

    private fun log(message: String) {
        Timber.tag("ConfigTAG").v(message)
    }
}

private const val CONFIG_DELAY = 5 * DateUtils.MINUTE_IN_MILLIS

private sealed interface ConfigResult {
    data object Success : ConfigResult
    data object FailNoNetwork : ConfigResult
    data object Fail : ConfigResult
    data class NotYetTime(val left: Long) : ConfigResult
}