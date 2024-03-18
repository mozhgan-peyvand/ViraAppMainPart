package ai.ivira.app.utils.common.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

const val AVANEGAR_FOLDER_PATH = "avanegar"
const val AVASHO_FOLDER_PATH = "avasho"
const val IMAZH_FOLDER_PATH = "imazh"
const val HAMAHANG_FOLDER_PATH = "hamahang"
const val VIRA_FOLDER_PATH = "vira"

const val MP3_EXTENSION = "mp3"
const val PNG_EXTENSION = "png"

class FileOperationHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getFile(
        fileName: String,
        path: String,
        extension: String
    ): File {
        val parent = File(context.filesDir, path)

        if (!parent.exists()) {
            parent.mkdirs()
        }

        return File(parent, "$fileName.$extension")
    }
}