package ai.ivira.app.utils.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun File.toBitmap(): Bitmap? {
    return if (this.exists()) {
        BitmapFactory.decodeFile(this.path)
    } else {
        null
    }
}