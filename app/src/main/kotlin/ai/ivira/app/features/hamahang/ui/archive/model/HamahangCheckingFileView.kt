package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.features.hamahang.data.entity.HamahangCheckingFileEntity

data class HamahangCheckingFileView(
    val id: String,
    override val title: String,
    val inputFilePath: String,
    val speaker: String,
    val isProper: Boolean,
    val createdAt: Long
) : HamahangArchiveView

fun HamahangCheckingFileEntity.toHamahangCheckingFileView() = HamahangCheckingFileView(
    id = id,
    title = title,
    inputFilePath = inputFilePath,
    speaker = speaker,
    isProper = isProper,
    createdAt = createdAt
)