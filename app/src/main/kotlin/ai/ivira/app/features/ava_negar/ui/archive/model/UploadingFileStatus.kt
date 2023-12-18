package ai.ivira.app.features.ava_negar.ui.archive.model

sealed interface UploadingFileStatus {
    data object Idle : UploadingFileStatus
    data object Uploading : UploadingFileStatus
    data object IsNotUploading : UploadingFileStatus
    data object FailureUpload : UploadingFileStatus
}