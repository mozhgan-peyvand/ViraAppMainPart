package ai.ivira.app.features.avasho.data.entity

data class AvashoArchiveFilesEntity(
    val processed: List<AvashoProcessedFileEntity>,
    val tracking: List<AvashoTrackingFileEntity>,
    val uploading: List<AvashoUploadingFileEntity>
)