package ai.ivira.app.features.hamahang.data.entity

data class HamahangUploadingFileEntity(
    val id: String,
    val title: String,
    val inputFilePath: String,
    val speaker: String,
    val createdAt: Long
)