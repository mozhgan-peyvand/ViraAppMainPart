package ai.ivira.app.features.avasho.ui.archive.model

import ai.ivira.app.features.avasho.data.entity.AvashoUploadingFileEntity

data class AvashoUploadingFileView(
    val id: String,
    override val title: String,
    val text: String,
    val speaker: String,
    val createdAt: Long
) : AvashoArchiveView

fun AvashoUploadingFileEntity.toAvashoUploadingFileView() = AvashoUploadingFileView(
    id = id,
    title = title,
    text = text,
    speaker = speaker,
    createdAt = createdAt
)