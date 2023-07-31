package ir.part.app.intelligentassistant.ui.screen.archive.entity

import ir.part.app.intelligentassistant.data.entity.AvanegarProcessedFileEntity

data class AvanegarProcessedFileView(
    val id: Int,
    val title: String,
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
