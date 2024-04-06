package ai.ivira.app.features.hamahang.data.entity

data class HamahangArchiveFilesEntity(
    val checking: List<HamahangCheckingFileEntity>,
    val processed: List<HamahangProcessedFileEntity>,
    val tracking: List<HamahangTrackingFileEntity>,
    val uploading: List<HamahangUploadingFileEntity>
)