package ai.ivira.app.features.imazh.ui.newImageDescriptor.model

import ai.ivira.app.features.ava_negar.ui.archive.model.convertDate
import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity

data class ImazhHistoryView(
    val prompt: String,
    val createdAt: String
)

fun ImazhHistoryEntity.toImazhHistoryView() = ImazhHistoryView(
    prompt = prompt,
    createdAt = convertDate(createdAt)
)