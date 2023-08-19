package ir.part.app.intelligentassistant.utils.common.file

import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadFileRequestBody(
    private val id: String,
    private val file: File,
    private val callback: UploadProgressCallback

) : RequestBody() {
    private val mimeTypeMap: MimeTypeMap by lazy(LazyThreadSafetyMode.NONE) {
        MimeTypeMap.getSingleton()
    }

    override fun contentType(): MediaType? {
        return mimeTypeMap.getMimeTypeFromExtension(file.extension)?.toMediaType()
    }

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {

        val total = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded = 0L
        FileInputStream(file).use { fis ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (fis.read(buffer).also { read = it } != -1) {
                handler.post {
                    callback.onProgress(
                        id,
                        uploaded,
                        total,

                        //fixme, what if total size is 0
                        total == uploaded
                    )
                }

                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048

    }

}