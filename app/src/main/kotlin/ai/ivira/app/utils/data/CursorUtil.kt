package ai.ivira.app.utils.data

import android.database.Cursor
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull

/**
 * Returns the value of the requested column as a String.
 * @throws IllegalArgumentException if columnIndex returns -1
 **/
fun Cursor.getStringColumn(column: String): String {
    val index = getColumnIndex(column)
    if (index == -1) {
        throw IllegalArgumentException("column index returned null for $column column")
    }
    return getString(index)
}

/**
 * Returns the value of the requested column as a Long.
 * @throws IllegalArgumentException if columnIndex returns -1
 **/
fun Cursor.getLongColumn(column: String): Long {
    val index = getColumnIndex(column)
    if (index == -1) {
        throw IllegalArgumentException("column index returned null for $column column")
    }
    return getLong(index)
}

/**
 * Returns the value of the requested column as a Int or null of not found
 **/
fun Cursor.getIntColumnOrNull(column: String): Int? {
    val index = getColumnIndex(column)
    if (index == -1) {
        return null
    }
    return getIntOrNull(index)
}

/**
 * Returns the value of the requested column as a Long or null of not found.
 * @throws IllegalArgumentException if columnIndex returns -1
 **/
fun Cursor.getLongColumnOrNull(column: String): Long? {
    val index = getColumnIndex(column)
    if (index == -1) {
        return null
    }
    return getLongOrNull(index)
}