package ai.ivira.app.utils.common.file

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

const val AVASHO_FOLDER_PATH = "avasho"
const val VIRA_FOLDER_PATH = "vira"

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

    fun copyFileToDownloadFolder(filePath: String, fileName: String): Boolean {
        val sourceFile = File(filePath)
        val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val parent = File(downloadFolder, VIRA_FOLDER_PATH)
        if (!parent.exists()) {
            parent.mkdirs()
        }

        var destinationPath = File(parent, "$fileName.${sourceFile.extension}")
        var counter = 1

        while (destinationPath.exists()) {
            destinationPath = File(parent, "${fileName}_$counter.${sourceFile.extension}")
            counter++
        }

        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null

        return try {
            fis = FileInputStream(sourceFile)
            fos = FileOutputStream(destinationPath)
            val buffer = ByteArray(1024)
            var length: Int

            while (fis.read(buffer).also { length = it } > 0) {
                fos.write(buffer, 0, length)
            }

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            fis?.close()
            fos?.close()
        }
    }
}