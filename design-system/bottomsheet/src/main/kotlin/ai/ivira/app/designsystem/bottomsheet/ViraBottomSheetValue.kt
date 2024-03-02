package ai.ivira.app.designsystem.bottomsheet

/**
 * Possible values of [ViraInternalBottomSheetState].
 */
enum class ViraBottomSheetValue {
    /**
     * The sheet is not visible.
     */
    Hidden,

    /**
     * The sheet is visible at full height.
     */
    Expanded,

    /**
     * The sheet is partially visible.
     */
    PartiallyExpanded
}