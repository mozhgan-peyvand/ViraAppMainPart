package ai.ivira.app.utils.ui

import ai.ivira.app.R
import android.content.Context
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Int.formatAsDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours))
            .append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":")
        .append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun formatDuration(duration: Long): String {
    val minutes: Long =
        TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds: Long =
        (
            TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
            )
    return String.format("%02d:%02d", minutes, seconds)
}

fun computeSecondAndMinute(second: Int) = if (second > 60) second / 60 else second
fun computeTextBySecondAndMinute(second: Int, context: Context) =
    if (second > 60) {
        context.getString(
            R.string.lbl_show_minute
        )
    } else {
        context.getString(R.string.lbl_show_second)
    }