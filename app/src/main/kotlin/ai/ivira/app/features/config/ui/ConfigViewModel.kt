package ai.ivira.app.features.config.ui

import ai.ivira.app.features.config.data.ConfigRepository
import ai.ivira.app.utils.ui.stateIn
import android.text.format.DateUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    configRepository: ConfigRepository
) : ViewModel() {
    private val tiles: StateFlow<List<TileItem>> = configRepository.getTiles()
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

    val hamahangTileConfig = tiles
        .map { it.firstOrNull { tile -> tile is TileItem.Hamahang } }
        .stateIn(initial = null)

    private val _shouldShowAvanegarUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowAvanegarUnavailableBottomSheet: State<Boolean> = _shouldShowAvanegarUnavailableBottomSheet

    private val _shouldShowAvashoUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowAvashoUnavailableBottomSheet: State<Boolean> = _shouldShowAvashoUnavailableBottomSheet

    private val _shouldShowImazhUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowImazhUnavailableBottomSheet: State<Boolean> = _shouldShowImazhUnavailableBottomSheet

    private val _shouldShowHamahangUnavailableBottomSheet = mutableStateOf(false)
    val shouldShowHamahangUnavailableBottomSheet: State<Boolean> = _shouldShowHamahangUnavailableBottomSheet

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

    fun showHamahangUnavailableFeature() {
        _shouldShowHamahangUnavailableBottomSheet.value = true
    }

    fun resetHamahangUnavailableFeature() {
        _shouldShowHamahangUnavailableBottomSheet.value = false
    }

    companion object {
        private const val UPDATE_DELAY_MS: Long = 12 * DateUtils.HOUR_IN_MILLIS
        private const val RETRY_DELAY_MS: Long = 30 * DateUtils.SECOND_IN_MILLIS
    }
}