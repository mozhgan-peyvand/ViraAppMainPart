package ir.part.app.intelligentassistant.data.entity

data class AvanegarArchiveUnionEntity(
    val id: Int,
    val title: String,
    val text: String,
    val createdAt: Long,
    val filePath: String,
    val token: String,
    val isSeen: Boolean
) {
    fun toAvanegarTrackingFileEntity() = AvanegarTrackingFileEntity(
        token = token,
        filePath = filePath,
        title = title,
        createdAt = createdAt
    )

    fun toAvanegarProcessedFileEntity() = AvanegarProcessedFileEntity(
        id = id,
        title = title,
        text = text,
        createdAt = createdAt,
        filePath = filePath,
        isSeen = isSeen
    )
}
