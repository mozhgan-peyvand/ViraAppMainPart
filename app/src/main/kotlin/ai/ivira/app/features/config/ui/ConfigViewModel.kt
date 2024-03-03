package ai.ivira.app.features.config.ui

import ai.ivira.app.features.config.data.ConfigRepository
import ai.ivira.app.utils.data.api_result.AppResult
import ai.ivira.app.utils.ui.stateIn
import android.text.format.DateUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val lastUpdateTime = configRepository.getLastTileConfigTime()
                val currentTime = System.currentTimeMillis()
                if (lastUpdateTime < 0 || currentTime - lastUpdateTime > UPDATE_DELAY_MS) {
                    if (configRepository.fetchTileConfigs() is AppResult.Success) {
                        delay(UPDATE_DELAY_MS)
                    } else {
                        delay(RETRY_DELAY_MS)
                    }
                } else {
                    delay(UPDATE_DELAY_MS - (currentTime - lastUpdateTime))
                }
            }
        }
    }

    private val tiles: StateFlow<List<TileItem>> = configRepository.getTileConfigs()
        .map { it.mapNotNull { entity -> entity.toTileItem() } }
        .stateIn(initial = emptyList())

    val avanegarTileConfig = tiles
        .map { it.firstOrNull { tile -> tile is TileItem.Avanegar } }
        .stateIn(initial = null)

    val avashoTileConfig = tiles
        .map { it.firstOrNull { tile -> tile is TileItem.Avasho } }
        .stateIn(initial = null)
    val imazhTileConfig = tiles
        .map { it.firstOrNull { tile -> tile is TileItem.Imazh } }
        .stateIn(initial = null)

    private val _shouldShowAvanegarUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowAvanegarUnavailableBottomSheet: State<Boolean> = _shouldShowAvanegarUnavailableBottomSheet

    private val _shouldShowAvashoUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowAvashoUnavailableBottomSheet: State<Boolean> = _shouldShowAvashoUnavailableBottomSheet

    private val _shouldShowImazhUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowImazhUnavailableBottomSheet: State<Boolean> = _shouldShowImazhUnavailableBottomSheet

    fun showAvanegarUnavailableFeature() {
        _shouldShowAvanegarUnavailableBottomSheet.value = true
    }

    fun resetAvanegarUnavailableFeature() {
        _shouldShowAvanegarUnavailableBottomSheet.value = false
    }

    fun showAvashoUnavailableFeature() {
        _shouldShowAvashoUnavailableBottomSheet.value = true
    }

    fun resetAvashoUnavailableFeature() {
        _shouldShowAvashoUnavailableBottomSheet.value = false
    }

    fun showImazhUnavailableFeature() {
        _shouldShowImazhUnavailableBottomSheet.value = true
    }

    fun resetImazhUnavailableFeature() {
        _shouldShowImazhUnavailableBottomSheet.value = false
    }

    companion object {
        private const val UPDATE_DELAY_MS: Long = 12 * DateUtils.HOUR_IN_MILLIS
        private const val RETRY_DELAY_MS: Long = 30 * DateUtils.SECOND_IN_MILLIS
    }
}