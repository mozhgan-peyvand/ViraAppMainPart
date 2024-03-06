package ai.ivira.app.designsystem.bottomsheet

import android.view.WindowManager
import androidx.compose.ui.window.SecureFlagPolicy

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
class ViraBottomSheetProperties(
    val securePolicy: SecureFlagPolicy,
    val isFocusable: Boolean,
    val shouldDismissOnBackPress: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ViraBottomSheetProperties) return false

        if (securePolicy != other.securePolicy) return false
        if (isFocusable != other.isFocusable) return false
        if (shouldDismissOnBackPress != other.shouldDismissOnBackPress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = securePolicy.hashCode()
        result = 31 * result + isFocusable.hashCode()
        result = 31 * result + shouldDismissOnBackPress.hashCode()
        return result
    }
}