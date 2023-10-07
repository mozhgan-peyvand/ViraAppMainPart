package ai.ivira.app.utils.common.file

interface UploadProgressCallback {
    fun onProgress(id: String, bytesUploaded: Long, totalBytes: Long, isDone: Boolean)
}