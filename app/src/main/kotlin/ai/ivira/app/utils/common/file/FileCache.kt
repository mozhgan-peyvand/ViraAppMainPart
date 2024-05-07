package ai.ivira.app.utils.common.file

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FileCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // content resolver
    private val contentResolver = context.contentResolver

    private val cacheLocation = File(context.filesDir, AVANEGAR_FOLDER_PATH)

    suspend fun cacheUri(uri: Uri): File? {
        return copyFromSource(uri)
    }

    suspend fun removeAllFiles() = suspendCoroutine<Unit> {
        context.cacheDir.walkBottomUp().forEach {
            if (it.exists()) {
                it.delete()
            }
        }
    }

    suspend fun removeAllViraFiles() = suspendCancellableCoroutine<Unit> { continuation ->
        val filesDir = context.filesDir
        var canceled = false
        if (filesDir.exists()) {
            listOf(
                File(filesDir, AVANEGAR_FOLDER_PATH),
                File(filesDir, AVASHO_FOLDER_PATH),
                File(filesDir, IMAZH_FOLDER_PATH),
                File(filesDir, "hamahang") // TODO: replace with const after  merging with Hamahang
            ).forEach parentForEach@{ dir ->
                if (canceled) return@parentForEach
                if (!dir.exists()) return@parentForEach

                dir.walkBottomUp().forEach { file ->
                    if (canceled) return@parentForEach
                    runCatching { if (file.exists()) file.delete() }
                }
            }
            continuation.resumeWith(Result.success(Unit))
        }

        continuation.invokeOnCancellation {
            canceled = true
        }
    }

    // save uri to a cache file
    private suspend fun copyFromSource(uri: Uri): File? = suspendCoroutine { continuation ->
        val fileExtension = uri.fileExtension(context)
        if (fileExtension == null) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        val fileName = uri.filename(context)

        val newFileName = if (!fileName.isNullOrBlank()) {
            var generatedName: String

            do {
                generatedName = "${System.currentTimeMillis()}_$fileName"
            } while (File(generatedName).exists())

            generatedName
        } else {
            generateFileName(fileExtension)
        }

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
            val outputFile = File(cacheLocation, newFileName)
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
        var name: String

        do {
            name = "${System.currentTimeMillis()}.$fileExtension"
        } while (File(cacheLocation, name).exists())

        return name
    }
}