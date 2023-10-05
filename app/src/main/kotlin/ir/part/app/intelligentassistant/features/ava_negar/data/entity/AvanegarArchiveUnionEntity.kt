package ir.part.app.intelligentassistant.features.ava_negar.data.entity

data class AvanegarArchiveUnionEntity(
    val id: Int,
    val uploadingId: String,
    val title: String,
    val text: String,
    val createdAt: Long,
    val filePath: String,
    val token: String,
    val fileDuration: Long,
    val isSeen: Boolean,
    val archiveType: String
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

    fun toAvanegarUploadingFileEntity() = AvanegarUploadingFileEntity(
        title = title,
        id = uploadingId,
        filePath = filePath,
        createdAt = createdAt,
        fileDuration = fileDuration
    )
}