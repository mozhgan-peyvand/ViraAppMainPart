package ir.part.app.intelligentassistant.ui.screen.archive

sealed interface UploadingFileStatus {
    object Idle : UploadingFileStatus
    object Uploading : UploadingFileStatus
    object IsNotUploading : UploadingFileStatus
    object FailureUpload : UploadingFileStatus
}