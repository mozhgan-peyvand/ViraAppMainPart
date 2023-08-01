package ir.part.app.intelligentassistant.utils.common.file

interface UploadProgressCallback {
    fun onProgress(bytesUploaded: Long, totalBytes: Long, isDone: Boolean)
}