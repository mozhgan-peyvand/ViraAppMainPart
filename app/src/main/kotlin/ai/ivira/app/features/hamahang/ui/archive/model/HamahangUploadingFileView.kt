package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.features.hamahang.data.entity.HamahangUploadingFileEntity

data class HamahangUploadingFileView(
    val id: String,
    override val title: String,
    val inputFilePath: String,
    val speaker: String,
    val createdAt: Long,
    val uploadingPercent: Float,
    val uploadedBytes: Long?
) : HamahangArchiveView

fun HamahangUploadingFileEntity.toHamahangUploadingFileView(
    uploadingId: String = "",
    uploadingPercent: Float = -1f,
    uploadedBytes: Long? = null
) = HamahangUploadingFileView(
    id = id,
    title = title,
    inputFilePath = inputFilePath,
    speaker = speaker,
    createdAt = createdAt,
    uploadedBytes = uploadedBytes,
    uploadingPercent = if (uploadingId == id) uploadingPercent else -1f // it's default and initial value,
)