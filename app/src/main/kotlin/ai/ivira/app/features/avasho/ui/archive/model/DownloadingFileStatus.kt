package ai.ivira.app.features.avasho.ui.archive.model

sealed interface DownloadingFileStatus {
    data object IdleDownload : DownloadingFileStatus
    data object Downloading : DownloadingFileStatus
    data object FailureDownload : DownloadingFileStatus
}