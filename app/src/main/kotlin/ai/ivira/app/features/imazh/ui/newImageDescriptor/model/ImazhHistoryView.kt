package ai.ivira.app.features.imazh.ui.newImageDescriptor.model

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.ui.archive.model.convertDate

data class ImazhHistoryView(
    val prompt: String,
    val createdAt: String
)

fun ImazhHistoryEntity.toImazhHistoryView() = ImazhHistoryView(
    prompt = prompt,
    createdAt = convertDate(createdAt)
)