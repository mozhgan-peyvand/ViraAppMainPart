package ir.part.app.intelligentassistant.utils.common

import android.content.SharedPreferences

fun SharedPreferences.safeGetInt(key: String, default: Int): Int {
    return kotlin.runCatching { getInt(key, default) }.getOrElse { default }
}