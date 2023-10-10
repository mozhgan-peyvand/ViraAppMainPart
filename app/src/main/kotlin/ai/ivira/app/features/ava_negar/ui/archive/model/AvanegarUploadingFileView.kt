package ai.ivira.app.features.ava_negar.ui.archive.model

import ai.ivira.app.features.ava_negar.data.entity.AvanegarUploadingFileEntity

data class AvanegarUploadingFileView(
    val id: String,
    override val title: String,
    val filePath: String,
    val createdAt: Long,
    val fileDuration: Long,
    val uploadedPercent: Float,
    val isUploadingFinished: Boolean
) : ArchiveView

fun AvanegarUploadingFileEntity.toAvanegarUploadingFileView(
    uploadedPercent: Float = 0f,
    uploadingId: String = ""
) = AvanegarUploadingFileView(
    id = id,
    title = title,
    filePath = filePath,
    createdAt = createdAt,
    fileDuration = fileDuration,
    uploadedPercent = if (uploadingId == id) uploadedPercent else 0f, // it's default and initial value
    isUploadingFinished = false // it's default and initial value
)