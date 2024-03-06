package ai.ivira.app.designsystem.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberViraBottomSheetState(
    skipPartiallyExpanded: Boolean = true,
    confirmValueChange: (ViraBottomSheetValue) -> Boolean = { true },
    initialValue: ViraBottomSheetValue = ViraBottomSheetValue.Hidden,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): ViraBottomSheetState {
    val skipHiddenState = false
    val density = LocalDensity.current
    val state = rememberViraInternalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
        skipHiddenState = skipHiddenState,
        initialValue = initialValue
    )
    return rememberSaveable(
        saver = ViraBottomSheetState.Saver(
            skipPartiallyExpanded = skipPartiallyExpanded,
            skipHiddenState = skipHiddenState,
            confirmValueChange = confirmValueChange,
            density = density,
            coroutineScope = coroutineScope
        )
    ) {
        ViraBottomSheetState(
            bottomSheetState = state,
            coroutineScope = coroutineScope
        )
    }
}

class ViraBottomSheetState internal constructor(
    internal val bottomSheetState: ViraInternalBottomSheetState,
    private val coroutineScope: CoroutineScope
) {
    val isVisible: Boolean get() = bottomSheetState.isVisible
    val currentValue: ViraBottomSheetValue get() = bottomSheetState.currentValue
    val targetValue: ViraBottomSheetValue get() = bottomSheetState.targetValue
    val hasExpandedState: Boolean get() = bottomSheetState.hasExpandedState

    var showBottomSheet by mutableStateOf(false)
        private set

    fun show() {
        showBottomSheet = true
    }

    fun hide() {
        coroutineScope.launch { bottomSheetState.hide() }
            .invokeOnCompletion {
                if (!bottomSheetState.isVisible) {
                    showBottomSheet = false
                }
            }
    }

    internal fun hide(showAnimation: Boolean) {
        if (showAnimation) {
            hide()
        } else {
            showBottomSheet = false
        }
    }

    companion object {
        internal fun Saver(
            skipPartiallyExpanded: Boolean,
            skipHiddenState: Boolean,
            confirmValueChange: (ViraBottomSheetValue) -> Boolean,
            density: Density,
            coroutineScope: CoroutineScope
        ): Saver<ViraBottomSheetState, Any> {
            return listSaver<ViraBottomSheetState, Any>(
                save = {
                    listOf(
                        it.showBottomSheet,
                        it.currentValue
                    )
                },
                restore = {
                    ViraBottomSheetState(
                        ViraInternalBottomSheetState(
                            density = density,
                            skipPartiallyExpanded = skipPartiallyExpanded,
                            skipHiddenState = skipHiddenState,
                            initialValue = it[1] as ViraBottomSheetValue,
                            confirmValueChange = confirmValueChange
                        ),
                        coroutineScope = coroutineScope
                    ).also { state ->
                        if (it[0] as Boolean) {
                            state.show()
                        }
                    }
                }
            )
        }
    }
}