package ir.part.app.intelligentassistant.data

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.data.entity.AvanegarTrackingFileEntity

data class AvanegarArchiveFilesEntity(
    val tracking: List<AvanegarTrackingFileEntity>,
    val processed: List<AvanegarProcessedFileEntity>
)