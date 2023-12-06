package ai.ivira.app.features.avasho.ui.search

import ai.ivira.app.features.ava_negar.ui.archive.model.convertDate
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.features.avasho.ui.archive.model.AvashoArchiveView

data class AvashoProcessedFileSearchView(
    val id: Int,
    val fileName: String,
    val createdAt: String
) : AvashoArchiveView

fun AvashoProcessedFileEntity.toProcessFileSearchView() = AvashoProcessedFileSearchView(
    id = id,
    fileName = fileName,
    createdAt = convertDate(createdAt)
)