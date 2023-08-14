package ir.part.app.intelligentassistant.utils.common.file

interface UploadProgressCallback {
    fun onProgress(id: String, bytesUploaded: Long, totalBytes: Long, isDone: Boolean)
}