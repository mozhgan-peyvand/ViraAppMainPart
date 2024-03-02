package ai.ivira.app.designsystem.bottomsheet

import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy

// TODO: read possible values from THEME!
@Immutable
object ViraBottomSheetDefaults {
    // FIXME: needed for edge-to-edge!
    /* Default insets to be used and consumed by the [ViraModalBottomSheet] window. */
    // val windowInsets: WindowInsets
    //     @Composable
    //     get() = WindowInsets.systemBars.only(WindowInsetsSides.Vertical)

    // The default max width used by [ViraModalBottomSheet]
    val SheetMaxWidth = 640.dp
    val ContainerShape: Shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
    val ContainerColor: Color = Color_BG_Bottom_Sheet
    val ScrimColor: Color = Color.Black.copy(alpha = 0.5f)

    // Drag Handle
    val DragHandleWidth: Dp = 32.dp
    val DragHandleHeight: Dp = 4.dp
    val DragHandleColor: Color = Color_On_Surface_Variant.copy(0.4f)
    val DragHandleShape: Shape = RoundedCornerShape(16.dp)

    /**
     * The optional visual marker placed on top of a bottom sheet to indicate it may be dragged.
     */
    @Composable
    fun DragHandle(
        modifier: Modifier = Modifier,
        width: Dp = DragHandleWidth,
        height: Dp = DragHandleHeight,
        shape: Shape = DragHandleShape,
        color: Color = DragHandleColor
    ) {
        val dragHandleDescription = "" // getString(Strings.BottomSheetDragHandleDescription)
        Surface(
            modifier = modifier
                .padding(vertical = DragHandleVerticalPadding)
                .semantics { contentDescription = dragHandleDescription },
            color = color,
            shape = shape
        ) {
            Box(
                Modifier
                    .size(
                        width = width,
                        height = height
                    )
            )
        }
    }

    /**
     * Properties used to customize the behavior of a [ViraBottomSheet].
     *
     * @param securePolicy Policy for setting [WindowManager.LayoutParams.FLAG_SECURE] on the bottom
     * sheet's window.
     * @param isFocusable Whether the modal bottom sheet is focusable. When true,
     * the modal bottom sheet will receive IME events and key presses, such as when
     * the back button is pressed.
     * @param shouldDismissOnBackPress Whether the modal bottom sheet can be dismissed by pressing
     * the back button. If true, pressing the back button will call onDismissRequest.
     * Note that [isFocusable] must be set to true in order to receive key events such as
     * the back button - if the modal bottom sheet is not focusable then this property does nothing.
     */
    fun properties(
        securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
        isFocusable: Boolean = true,
        shouldDismissOnBackPress: Boolean = true
    ) = ViraBottomSheetProperties(securePolicy, isFocusable, shouldDismissOnBackPress)
}

private val DragHandleVerticalPadding = 22.dp