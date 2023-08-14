package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarUploadingFileEntity

data class AvanegarArchiveFilesEntity(
    val tracking: List<AvanegarTrackingFileEntity>,
    val processed: List<AvanegarProcessedFileEntity>,
    val uploading: List<AvanegarUploadingFileEntity>
)