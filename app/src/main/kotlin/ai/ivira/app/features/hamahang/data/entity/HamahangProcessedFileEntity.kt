package ai.ivira.app.features.hamahang.data.entity

data class HamahangProcessedFileEntity(
    val id: Int,
    val title: String,
    val fileUrl: String,
    val filePath: String,
    val inputFilePath: String,
    val speaker: String,
    val createdAt: Long,
    val isSeen: Boolean
)