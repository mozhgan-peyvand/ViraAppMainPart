package ir.part.app.intelligentassistant.utils.common.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap


fun Uri.fileExtension(context: Context): String? {
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(context.contentResolver.getType(this))
}

fun Uri.filename(context: Context): String? {
    val contentResolver = context.contentResolver
    return contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}