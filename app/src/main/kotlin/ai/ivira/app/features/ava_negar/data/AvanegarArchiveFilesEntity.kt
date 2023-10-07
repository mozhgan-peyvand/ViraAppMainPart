package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarProcessedFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity

data class AvanegarArchiveFilesEntity(
    val tracking: List<AvanegarTrackingFileEntity>,
    val processed: List<AvanegarProcessedFileEntity>,
    val uploading: List<AvanegarUploadingFileEntity>
)