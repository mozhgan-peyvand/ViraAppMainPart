package ai.ivira.app.features.hamahang.data.entity

data class HamahangArchiveFilesEntity(
    val processed: List<HamahangProcessedFileEntity>,
    val tracking: List<HamahangTrackingFileEntity>,
    val uploading: List<HamahangUploadingFileEntity>
)