package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model

import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarProcessedFileEntity

data class AvanegarProcessedFileView(
    val id: Int,
    override val title: String,
    val text: String,
    val createdAt: String,
    val filePath: String,
    val isSeen: Boolean
) : ArchiveView

fun AvanegarProcessedFileEntity.toAvanegarProcessedFileView() = AvanegarProcessedFileView(
    id = id,
    title = title,
    text = text,
    createdAt = convertDate(createdAt),
    filePath = filePath,
    isSeen = isSeen
)