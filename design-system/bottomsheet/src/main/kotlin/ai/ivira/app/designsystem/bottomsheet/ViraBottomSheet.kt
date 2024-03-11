package ai.ivira.app.designsystem.bottomsheet

import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Expanded
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Hidden
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.PartiallyExpanded
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.max

/* FIXME: current implementation does not support edge-to-edge */
// in order to add support for edge to edge search fixme in the related files

/**
 * <a href="https://m3.material.io/components/bottom-sheets/overview" class="external" target="_blank">Material Design modal bottom sheet</a>.
 *
 * Modal bottom sheets are used as an alternative to inline menus or simple dialogs on mobile,
 * especially when offering a long list of action items, or when items require longer descriptions
 * and icons. Like dialogs, modal bottom sheets appear in front of app content, disabling all other
 * app functionality when they appear, and remaining on screen until confirmed, dismissed, or a
 * required action has been taken.
 *
 * ![Bottom sheet image](https://developer.android.com/images/reference/androidx/compose/material3/bottom_sheet.png)
 *
 * A simple example of a modal bottom sheet looks like this:
 **
 * animates to [Hidden].
 * @param modifier Optional [Modifier] for the bottom sheet.
 * @param sheetState The state of the bottom sheet.
 * @param sheetMaxWidth [Dp] that defines what the maximum width the sheet will take.
 * Pass in [Dp.Unspecified] for a sheet that spans the entire screen width.
 * @param shape The shape of the bottom sheet.
 * @param containerColor The color used for the background of this bottom sheet
 * @param contentColor The preferred color for content inside this bottom sheet. Defaults to either
 * the matching content color for [containerColor], or to the current [LocalContentColor] if
 * [containerColor] is not a color from the theme.
 * @param scrimColor Color of the scrim that obscures content when the bottom sheet is open.
 * @param dragHandle Optional visual marker to swipe the bottom sheet.
 * params.
 * @param properties [ViraBottomSheetProperties] for further customization of this
 * modal bottom sheet's behavior.
 * @param onBackPressed can be used when  [ViraBottomSheetProperties.shouldDismissOnBackPress] is set to false,
 * this callback will be called we the user presses the back button while bottom sheet is open.
 * hiding the bottomSheet must be done manually at that point.
 * @param content The content to be displayed inside the bottom sheet.
 */
@Composable
fun ViraBottomSheet(
    sheetState: ViraBottomSheetState,
    modifier: Modifier = Modifier,
    isDismissibleOnTouchOutside: Boolean = true,
    isDismissibleOnDrag: Boolean = true,
    sheetMaxWidth: Dp = ViraBottomSheetDefaults.SheetMaxWidth,
    shape: Shape = ViraBottomSheetDefaults.ContainerShape,
    containerColor: Color = ViraBottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = ViraBottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = null, // = { BottomSheetDefaults.DragHandle() },
    properties: ViraBottomSheetProperties = ViraBottomSheetDefaults.properties(),
    onBackPressed: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    ViraBottomSheet(
        onDismissRequest = {
            sheetState.hide(false)
        },
        sheetState = sheetState.bottomSheetState,
        modifier = modifier,
        isDismissibleOnTouchOutside = isDismissibleOnTouchOutside,
        isDismissibleOnDrag = isDismissibleOnDrag,
        sheetMaxWidth = sheetMaxWidth,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        properties = properties,
        onBackPressed = onBackPressed,
        content = content
    )
}

@Composable
internal fun ViraBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isDismissibleOnTouchOutside: Boolean = true,
    isDismissibleOnDrag: Boolean = true,
    sheetState: ViraInternalBottomSheetState = rememberViraInternalBottomSheetState(),
    sheetMaxWidth: Dp = ViraBottomSheetDefaults.SheetMaxWidth,
    shape: Shape = ViraBottomSheetDefaults.ContainerShape,
    containerColor: Color = ViraBottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = ViraBottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = null, // = { BottomSheetDefaults.DragHandle() },
    // windowInsets: WindowInsets = BottomSheetDefaults.windowInsets, // FIXME: needed for edge-to-edge
    properties: ViraBottomSheetProperties = ViraBottomSheetDefaults.properties(),
    onBackPressed: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val animateToDismiss: () -> Unit = {
        if (sheetState.anchoredDraggableState.confirmValueChange(Hidden)) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
    }
    val settleToDismiss: (velocity: Float) -> Unit = {
        scope.launch { sheetState.settle(it) }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismissRequest()
        }
    }

    ViraModalBottomSheetPopup(
        properties = properties,
        onBackPressed = onBackPressed,
        onDismissRequest = {
            if (sheetState.currentValue == Expanded && sheetState.hasPartiallyExpandedState) {
                scope.launch { sheetState.partialExpand() }
            } else { // Is expanded without collapsed state or is collapsed.
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            }
        }
        // windowInsets = windowInsets // FIXME: needed for edge-to-edge
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val fullHeight = constraints.maxHeight
            Scrim(
                color = scrimColor,
                isDismissible = isDismissibleOnTouchOutside,
                onDismissRequest = animateToDismiss,
                visible = sheetState.targetValue != Hidden
            )
            val bottomSheetPaneTitle = "" // getString(string = Strings.BottomSheetPaneTitle)
            Surface(
                modifier = modifier
                    .widthIn(max = sheetMaxWidth)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .semantics { paneTitle = bottomSheetPaneTitle }
                    .offset {
                        IntOffset(
                            0,
                            sheetState
                                .requireOffset()
                                .toInt()
                        )
                    }
                    .nestedScroll(
                        remember(sheetState) {
                            consumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                                sheetState = sheetState,
                                orientation = Orientation.Vertical,
                                onFling = settleToDismiss
                            )
                        }
                    )
                    .draggable(
                        state = sheetState.anchoredDraggableState.draggableState,
                        orientation = Orientation.Vertical,
                        enabled = sheetState.isVisible && isDismissibleOnDrag,
                        startDragImmediately = sheetState.anchoredDraggableState.isAnimationRunning,
                        onDragStopped = { settleToDismiss(it) }
                    )
                    .modalBottomSheetAnchors(
                        sheetState = sheetState,
                        fullHeight = fullHeight.toFloat()
                    ),
                shape = shape,
                color = containerColor,
                contentColor = contentColor
            ) {
                Column(Modifier.fillMaxWidth()) {
                    if (dragHandle != null) {
                        val collapseActionLabel = ""
                        // getString(Strings.BottomSheetPartialExpandDescription)
                        val dismissActionLabel = "" // getString(Strings.BottomSheetDismissDescription)
                        val expandActionLabel = "" // getString(Strings.BottomSheetExpandDescription)
                        Box(
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .semantics(mergeDescendants = true) {
                                    // Provides semantics to interact with the bottomsheet based on its
                                    // current value.
                                    with(sheetState) {
                                        dismiss(dismissActionLabel) {
                                            animateToDismiss()
                                            true
                                        }
                                        if (currentValue == PartiallyExpanded) {
                                            expand(expandActionLabel) {
                                                if (anchoredDraggableState.confirmValueChange(
                                                        Expanded
                                                    )
                                                ) {
                                                    scope.launch { sheetState.expand() }
                                                }
                                                true
                                            }
                                        } else if (hasPartiallyExpandedState) {
                                            collapse(collapseActionLabel) {
                                                if (anchoredDraggableState.confirmValueChange(
                                                        PartiallyExpanded
                                                    )
                                                ) {
                                                    scope.launch { partialExpand() }
                                                }
                                                true
                                            }
                                        }
                                    }
                                }
                        ) {
                            dragHandle()
                        }
                    }
                    content()
                }
            }
        }
    }
    if (sheetState.hasExpandedState) {
        LaunchedEffect(sheetState) {
            sheetState.show()
        }
    }
}

@Composable
private fun Scrim(
    color: Color,
    isDismissible: Boolean,
    onDismissRequest: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec(),
            label = "scrimColor"
        )

        val dismissModifier = Modifier.then(
            if (visible && isDismissible) {
                Modifier.pointerInput(onDismissRequest) {
                    detectTapGestures {
                        onDismissRequest()
                    }
                }
            } else {
                Modifier
            }
        )

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

private fun Modifier.modalBottomSheetAnchors(
    sheetState: ViraInternalBottomSheetState,
    fullHeight: Float
) = onSizeChanged { sheetSize ->

    val newAnchors = DraggableAnchors {
        Hidden at fullHeight
        if (sheetSize.height > (fullHeight / 2) && !sheetState.skipPartiallyExpanded) {
            PartiallyExpanded at fullHeight / 2f
        }
        if (sheetSize.height != 0) {
            Expanded at max(0f, fullHeight - sheetSize.height)
        }
    }

    val newTarget = when (sheetState.anchoredDraggableState.targetValue) {
        Hidden -> Hidden
        PartiallyExpanded, Expanded -> {
            val hasPartiallyExpandedState = newAnchors.hasAnchorFor(PartiallyExpanded)
            val newTarget = if (hasPartiallyExpandedState) {
                PartiallyExpanded
            } else if (newAnchors.hasAnchorFor(Expanded)) {
                Expanded
            } else {
                Hidden
            }
            newTarget
        }
    }

    sheetState.anchoredDraggableState.updateAnchors(newAnchors, newTarget)
}

/**
 * Popup specific for modal bottom sheet.
 */
@Composable
internal fun ViraModalBottomSheetPopup(
    properties: ViraBottomSheetProperties,
    onDismissRequest: () -> Unit,
    onBackPressed: () -> Unit,
    // windowInsets: WindowInsets, // FIXME: needed for edge-to-edge
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val id = rememberSaveable { UUID.randomUUID() }
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val layoutDirection = LocalLayoutDirection.current
    val modalBottomSheetWindow = remember {
        ViraModalBottomSheetWindow(
            properties = properties,
            onDismissRequest = onDismissRequest,
            onBackPressed = onBackPressed,
            composeView = view,
            saveId = id
        ).apply {
            setCustomContent(
                parent = parentComposition,
                content = {
                    Box(
                        Modifier
                            .semantics { this.popup() }
                        // FIXME: needed for edge-to-edge
                        // .windowInsetsPadding(windowInsets)
                        // .then(
                        //     // TODO(b/290893168): Figure out a solution for APIs < 30.
                        //     if (Build.VERSION.SDK_INT >= 33) {
                        //         Modifier.imePadding()
                        //     } else {
                        //         Modifier
                        //     }
                        // )
                    ) {
                        currentContent()
                    }
                }
            )
        }
    }

    DisposableEffect(modalBottomSheetWindow) {
        modalBottomSheetWindow.show()
        modalBottomSheetWindow.superSetLayoutDirection(layoutDirection)
        onDispose {
            modalBottomSheetWindow.disposeComposition()
            modalBottomSheetWindow.dismiss()
        }
    }
}

/** Custom compose view for [ViraBottomSheet] */
@SuppressLint("ViewConstructor")
private class ViraModalBottomSheetWindow(
    private val properties: ViraBottomSheetProperties,
    private var onDismissRequest: () -> Unit,
    private var onBackPressed: () -> Unit,
    private val composeView: View,
    saveId: UUID
) : AbstractComposeView(composeView.context),
    ViewTreeObserver.OnGlobalLayoutListener,
    ViewRootForInspector {
    private var backCallback: Any? = null

    init {
        id = android.R.id.content
        // Set up view owners
        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "Popup:$saveId")
        // Enable children to draw their shadow by not clipping them
        clipChildren = false
    }

    private val windowManager =
        composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val displayWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    private val params: WindowManager.LayoutParams =
        WindowManager.LayoutParams().apply {
            // Position bottom sheet from the bottom of the screen
            gravity = Gravity.BOTTOM or Gravity.START
            // Application panel window
            type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
            // Fill up the entire app view
            width = displayWidth
            height = WindowManager.LayoutParams.MATCH_PARENT

            // Format of screen pixels
            format = PixelFormat.TRANSLUCENT
            // Title used as fallback for a11y services
            // TODO: Provide bottom sheet window resource
            title = composeView.context.resources.getString(R.string.default_popup_window_title)
            // Get the Window token from the parent view
            token = composeView.applicationWindowToken

            // Flags specific to modal bottom sheet.
            flags = flags and (
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                ).inv()

            // FIXME: needed for edge-to-edge
            // flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

            // Security flag
            val secureFlagEnabled =
                properties.securePolicy.shouldApplySecureFlag(composeView.isFlagSecureEnabled())
            flags = if (secureFlagEnabled) {
                flags or WindowManager.LayoutParams.FLAG_SECURE
            } else {
                flags and (WindowManager.LayoutParams.FLAG_SECURE.inv())
            }

            // Focusable
            flags = if (!properties.isFocusable) {
                flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            } else {
                flags and (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
            }
        }

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        content()
    }

    fun setCustomContent(
        parent: CompositionContext? = null,
        content: @Composable () -> Unit
    ) {
        parent?.let { setParentCompositionContext(it) }
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    fun show() {
        windowManager.addView(this, params)
    }

    fun dismiss() {
        setViewTreeLifecycleOwner(null)
        setViewTreeSavedStateRegistryOwner(null)
        composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        windowManager.removeViewImmediate(this)
    }

    /**
     * Taken from PopupWindow. Calls [onDismissRequest] when back button is pressed.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyDispatcherState == null) {
                return super.dispatchKeyEvent(event)
            }
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                if (state != null && state.isTracking(event) && !event.isCanceled) {
                    if (!properties.shouldDismissOnBackPress) {
                        onBackPressed()
                    } else {
                        onDismissRequest()
                    }
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        maybeRegisterBackCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        maybeUnregisterBackCallback()
    }

    private fun maybeRegisterBackCallback() {
        if (!properties.shouldDismissOnBackPress || Build.VERSION.SDK_INT < 33) {
            return
        }
        if (backCallback == null) {
            backCallback = Api33Impl.createBackCallback(onDismissRequest)
        }
        Api33Impl.maybeRegisterBackCallback(this, backCallback)
    }

    private fun maybeUnregisterBackCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            Api33Impl.maybeUnregisterBackCallback(this, backCallback)
        }
        backCallback = null
    }

    override fun onGlobalLayout() {
        // No-op
    }

    override fun setLayoutDirection(layoutDirection: Int) {
        // Do nothing. ViewRootImpl will call this method attempting to set the layout direction
        // from the context's locale, but we have one already from the parent composition.
    }

    // Sets the "real" layout direction for our content that we obtain from the parent composition.
    fun superSetLayoutDirection(layoutDirection: LayoutDirection) {
        val direction = when (layoutDirection) {
            LayoutDirection.Ltr -> android.util.LayoutDirection.LTR
            LayoutDirection.Rtl -> android.util.LayoutDirection.RTL
        }
        super.setLayoutDirection(direction)
    }

    @RequiresApi(33)
    private object Api33Impl {
        @JvmStatic
        @DoNotInline
        fun createBackCallback(onDismissRequest: () -> Unit) =
            OnBackInvokedCallback(onDismissRequest)

        @JvmStatic
        @DoNotInline
        fun maybeRegisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_OVERLAY,
                    backCallback
                )
            }
        }

        @JvmStatic
        @DoNotInline
        fun maybeUnregisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.unregisterOnBackInvokedCallback(backCallback)
            }
        }
    }
}