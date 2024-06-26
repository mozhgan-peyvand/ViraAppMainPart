package ai.ivira.app.utils.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

interface ViraTooltip

class TooltipHelper(
    private val onAllTooltipsShown: () -> Unit,
    private val scrollToPosition: suspend (Int) -> Unit
) {
    private val tooltips = mutableStateListOf<TooltipData>()
    private var isSingleTooltip = true
    private var lastPosition = Int.MIN_VALUE
    private val _isTooltipRunning = mutableStateOf(false)
    val isTooltipRunning: State<Boolean> = _isTooltipRunning

    suspend fun setupTooltipChainRunner(
        tooltips: List<Pair<ViraTooltip, Int?>> // List of tooltip type and position for scroll
    ) {
        this.tooltips.clear()
        isSingleTooltip = tooltips.size < 2

        this.tooltips.addAll(
            tooltips.map {
                TooltipData(
                    type = it.first,
                    showState = mutableStateOf(tooltips.firstOrNull() == it),
                    positionForScroll = it.second
                )
            }
        )
        this.tooltips.firstOrNull()?.positionForScroll?.let { scrollToPosition(it) }
        _isTooltipRunning.value = tooltips.isNotEmpty()
    }

    suspend fun setupSingleTooltipRunner(
        tooltip: ViraTooltip,
        tooltipPosition: Int? = null
    ) {
        setupTooltipChainRunner(listOf(Pair(tooltip, tooltipPosition)))
    }

    fun getTooltipStateByKey(key: ViraTooltip) = tooltips.firstOrNull { it.type == key }?.showState

    suspend fun next() {
        tooltips.removeFirstOrNull()?.showState?.value = false

        tooltips.firstOrNull()?.positionForScroll?.let {
            if (lastPosition > it) {
                scrollToPosition(it + 200)
            } else {
                scrollToPosition(it)
            }
            lastPosition = it
        }
        tooltips.firstOrNull()?.showState?.value = true

        if (tooltips.size == 0) {
            onAllTooltipsShown().also {
                if (!isSingleTooltip) scrollToPosition(0)
            }
            _isTooltipRunning.value = false
        }
    }
}

private data class TooltipData(
    val type: ViraTooltip,
    val showState: MutableState<Boolean>,
    val positionForScroll: Int?
)