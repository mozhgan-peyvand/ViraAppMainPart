package ai.ivira.app.designsystem.bottomsheet

import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Expanded
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Hidden
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.PartiallyExpanded
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.coroutines.cancellation.CancellationException

@Composable
internal fun rememberViraInternalBottomSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (ViraBottomSheetValue) -> Boolean = { true },
    initialValue: ViraBottomSheetValue = Hidden,
    skipHiddenState: Boolean = false
): ViraInternalBottomSheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded, confirmValueChange,
        saver = ViraInternalBottomSheetState.Saver(
            skipPartiallyExpanded = skipPartiallyExpanded,
            skipHiddenState = skipHiddenState,
            confirmValueChange = confirmValueChange,
            density = density
        )
    ) {
        ViraInternalBottomSheetState(
            density = density,
            skipPartiallyExpanded = skipPartiallyExpanded,
            skipHiddenState = skipHiddenState,
            initialValue = initialValue,
            confirmValueChange = confirmValueChange
        )
    }
}

/**
 * State of a sheet composable, such as [ViraBottomSheet]
 *
 * Contains states relating to its swipe position as well as animations between state values.
 *
 * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is large
 * enough, should be skipped. If true, the sheet will always expand to the [Expanded] state and move
 * to the [Hidden] state if available when hiding the sheet, either programmatically or by user
 * interaction.
 * @param initialValue The initial value of the state.
 * @param density The density that this state can use to convert values to and from dp.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 * @param skipHiddenState Whether the hidden state should be skipped. If true, the sheet will always
 * expand to the [Expanded] state and move to the [PartiallyExpanded] if available, either
 * programmatically or by user interaction.
 */
@Stable
internal class ViraInternalBottomSheetState(
    private val density: Density,
    internal val skipPartiallyExpanded: Boolean,
    internal val skipHiddenState: Boolean = false,
    initialValue: ViraBottomSheetValue = Hidden,
    confirmValueChange: (ViraBottomSheetValue) -> Boolean = { true }
) {
    init {
        if (skipPartiallyExpanded) {
            require(initialValue != PartiallyExpanded) {
                "The initial value must not be set to PartiallyExpanded if skipPartiallyExpanded " +
                    "is set to true."
            }
        }
        if (skipHiddenState) {
            require(initialValue != Hidden) {
                "The initial value must not be set to Hidden if skipHiddenState is set to true."
            }
        }
    }

    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the state the bottom sheet is
     * currently in. If a swipe or an animation is in progress, this corresponds the state the sheet
     * was in before the swipe or animation started.
     */

    val currentValue: ViraBottomSheetValue get() = anchoredDraggableState.currentValue

    /**
     * The target value of the bottom sheet state.
     *
     * If a swipe is in progress, this is the value that the sheet would animate to if the
     * swipe finishes. If an animation is running, this is the target value of that animation.
     * Finally, if no swipe or animation is in progress, this is the same as the [currentValue].
     */
    val targetValue: ViraBottomSheetValue get() = anchoredDraggableState.targetValue

    /**
     * Whether the modal bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = anchoredDraggableState.currentValue != Hidden

    /**
     * Require the current offset (in pixels) of the bottom sheet.
     *
     * The offset will be initialized during the first measurement phase of the provided sheet
     * content.
     *
     * These are the phases:
     * Composition { -> Effects } -> Layout { Measurement -> Placement } -> Drawing
     *
     * During the first composition, an [IllegalStateException] is thrown. In subsequent
     * compositions, the offset will be derived from the anchors of the previous pass. Always prefer
     * accessing the offset from a LaunchedEffect as it will be scheduled to be executed the next
     * frame, after layout.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /**
     * Whether the sheet has an expanded state defined.
     */

    val hasExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(Expanded)

    val progress: Float
        get() = anchoredDraggableState.progress

    /**
     * Whether the modal bottom sheet has a partially expanded state defined.
     */
    val hasPartiallyExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(PartiallyExpanded)

    /**
     * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun expand() {
        anchoredDraggableState.animateTo(Expanded)
    }

    /**
     * Animate the bottom sheet and suspend until it is partially expanded or animation has been
     * cancelled.
     * @throws [CancellationException] if the animation is interrupted
     * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
     */
    suspend fun partialExpand() {
        check(!skipPartiallyExpanded) {
            "Attempted to animate to partial expanded when skipPartiallyExpanded was enabled. Set" +
                " skipPartiallyExpanded to false to use this function."
        }
        animateTo(PartiallyExpanded)
    }

    /**
     * Expand the bottom sheet with animation and suspend until it is [PartiallyExpanded] if defined
     * else [Expanded].
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun show() {
        val targetValue = when {
            hasPartiallyExpandedState -> PartiallyExpanded
            else -> Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it is fully hidden or animation has
     * been cancelled.
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() {
        check(!skipHiddenState) {
            "Attempted to animate to hidden when skipHiddenState was enabled. Set skipHiddenState" +
                " to false to use this function."
        }
        animateTo(Hidden)
    }

    /**
     * Animate to a [targetValue].
     * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
     * [targetValue] without updating the offset.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    internal suspend fun animateTo(
        targetValue: ViraBottomSheetValue,
        velocity: Float = anchoredDraggableState.lastVelocity
    ) {
        anchoredDraggableState.animateTo(targetValue, velocity)
    }

    /**
     * Snap to a [targetValue] without any animation.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    internal suspend fun snapTo(targetValue: ViraBottomSheetValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    /**
     * Find the closest anchor taking into account the velocity and settle at it with an animation.
     */
    internal suspend fun settle(velocity: Float) {
        anchoredDraggableState.settle(velocity)
    }

    internal var anchoredDraggableState = AnchoredDraggableState(
        initialValue = initialValue,
        animationSpec = AnchoredDraggableDefaults.AnimationSpec,
        confirmValueChange = confirmValueChange,
        positionalThreshold = { with(density) { 56.dp.toPx() } },
        velocityThreshold = { with(density) { 125.dp.toPx() } }
    )

    internal val offset: Float get() = anchoredDraggableState.offset

    companion object {
        /**
         * The default [Saver] implementation for [ViraInternalBottomSheetState].
         */
        fun Saver(
            skipPartiallyExpanded: Boolean,
            skipHiddenState: Boolean,
            confirmValueChange: (ViraBottomSheetValue) -> Boolean,
            density: Density
        ) =
            androidx.compose.runtime.saveable.Saver<ViraInternalBottomSheetState, ViraBottomSheetValue>(
                save = { it.currentValue },
                restore = { savedValue ->
                    ViraInternalBottomSheetState(
                        density = density,
                        skipHiddenState = skipHiddenState,
                        skipPartiallyExpanded = skipPartiallyExpanded,
                        initialValue = savedValue,
                        confirmValueChange = confirmValueChange
                    )
                }
            )
    }
}