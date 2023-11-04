package ai.ivira.app.utils.common.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val AVASHO_FOLDER_PATH = "avasho"

class FileOperationHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getFile(
        fileName: String,
        path: String
    ): File {
        val parent = File(context.filesDir, path)

        if (!parent.exists()) {
            parent.mkdirs()
        }

        return File(parent, "$fileName.mp3")
    }
}