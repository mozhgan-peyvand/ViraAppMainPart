package ai.ivira.app.utils.common.file

import ai.ivira.app.R
import ai.ivira.app.utils.ui.StorageUtils
import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class SaveToDownloadsHelper @Inject constructor(
    private val storageUtils: StorageUtils
) {
    fun saveToDownloadFolder(filePath: String, fileName: String): SaveToDownloadResult {
        if (storageUtils.getAvailableSpace() <= File(filePath).length()) {
            return SaveToDownloadResult.Error.NotEnoughSpace
        }

        val isSuccess = copyFileToDownloadFolder(
            filePath = filePath,
            fileName = fileName
        )

        return if (isSuccess == null) {
            SaveToDownloadResult.Success
        } else {
            SaveToDownloadResult.Error.IOException(isSuccess)
        }
    }

    private fun copyFileToDownloadFolder(filePath: String, fileName: String): IOException? {
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

            null
        } catch (e: IOException) {
            e.printStackTrace()
            e
        } finally {
            fis?.close()
            fos?.close()
        }
    }
}

sealed interface SaveToDownloadResult {
    data object Success : SaveToDownloadResult
    sealed interface Error : SaveToDownloadResult {
        data object NotEnoughSpace : Error
        data class IOException(val e: java.io.IOException) : Error

        fun getErrorMessage(context: Context): String {
            return when (this) {
                is NotEnoughSpace -> context.getString(R.string.msg_not_enough_space)
                is IOException -> context.getString(R.string.msg_convert_error)
            }
        }
    }

    val isSuccess: Boolean
        get() = this is Success

    val isFailure: Boolean
        get() = this is Error

    fun onFailure(block: (Error) -> Unit): SaveToDownloadResult {
        if (this is Error) {
            block(this)
        }
        return this
    }

    fun onSuccess(block: (Success) -> Unit): SaveToDownloadResult {
        if (this is Success) {
            block(this)
        }
        return this
    }
}