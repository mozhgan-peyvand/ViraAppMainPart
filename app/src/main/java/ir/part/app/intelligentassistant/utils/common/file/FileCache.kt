package ir.part.app.intelligentassistant.utils.common.file

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FileCache @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // content resolver
    private val contentResolver = context.contentResolver

    private val cacheLocation = context.cacheDir


    suspend fun cacheUri(uri: Uri): File? {
        return copyFromSource(uri)
    }

    suspend fun removeAll() = suspendCoroutine<Unit> {
        context.cacheDir.walkBottomUp().forEach {
            if (it.exists()) {
                it.delete()
            }
        }
    }

    // save uri to a cache file
    private suspend fun copyFromSource(uri: Uri): File? = suspendCoroutine { continuation ->
        val fileExtension = uri.fileExtension(context)
        if (fileExtension == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        val fileName = uri.filename(context) ?: generateFileName(fileExtension)

        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        try {
            if (!cacheLocation.exists()) {
                cacheLocation.mkdirs()
            }
            // the file which will be the new cached file
            val outputFile = File(cacheLocation, fileName)
            if (outputFile.exists()) {
                outputFile.delete()
            }
            outputFile.createNewFile()
            inputStream.copyTo(outputFile.outputStream())
            continuation.resume(outputFile)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            continuation.resume(null)
        } finally {
            inputStream.close()
        }
    }

    private fun generateFileName(fileExtension: String): String {
        return "${System.currentTimeMillis()}.$fileExtension"
    }
}