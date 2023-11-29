package ai.ivira.app.utils.ui

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class StorageUtils @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getAvailableSpace(): Long {
        return kotlin.runCatching {
            val externalFilesDir: File = getExternalFilesDir() ?: return 0L
            val statFs = StatFs(externalFilesDir.path)
            statFs.blockSizeLong * statFs.availableBlocksLong
        }.getOrElse { 0L }
    }

    private fun getExternalFilesDir(): File? {
        return if (isExternalStorageWritable()) {
            // Use getExternalFilesDir to get the external storage directory for your app
            context.getExternalFilesDir(null)
        } else {
            null
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}