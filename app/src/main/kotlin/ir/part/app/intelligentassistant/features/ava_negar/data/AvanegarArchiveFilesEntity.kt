package ir.part.app.intelligentassistant.features.ava_negar.data

import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarUploadingFileEntity

data class AvanegarArchiveFilesEntity(
    val tracking: List<AvanegarTrackingFileEntity>,
    val processed: List<AvanegarProcessedFileEntity>,
    val uploading: List<AvanegarUploadingFileEntity>
)