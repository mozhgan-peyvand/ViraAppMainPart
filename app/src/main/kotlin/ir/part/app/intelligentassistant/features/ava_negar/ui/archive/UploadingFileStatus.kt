package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

sealed interface UploadingFileStatus {
    object Idle : UploadingFileStatus
    object Uploading : UploadingFileStatus
    object IsNotUploading : UploadingFileStatus
    object FailureUpload : UploadingFileStatus
}