package ir.part.app.intelligentassistant.utils.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String, isLong: Boolean = false) {
    val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, message, duration).show()
}

fun Context.showText(@StringRes message: Int, isLong: Boolean = false) {
    showToast(getString(message), isLong)
}