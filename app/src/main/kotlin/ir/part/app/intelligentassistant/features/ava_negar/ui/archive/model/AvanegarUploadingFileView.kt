package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model

import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarUploadingFileEntity

data class AvanegarUploadingFileView(
    val id: String,
    override val title: String,
    val filePath: String,
    val createdAt: Long,
    val uploadedPercent: Float,
    val isUploadingFinished: Boolean,
) : ArchiveView

fun AvanegarUploadingFileEntity.toAvanegarUploadingFileView() = AvanegarUploadingFileView(
    id = id,
    title = title,
    filePath = filePath,
    createdAt = createdAt,
    uploadedPercent = 0f, //it's default and initial value
    isUploadingFinished = false, //it's default and initial value
)
