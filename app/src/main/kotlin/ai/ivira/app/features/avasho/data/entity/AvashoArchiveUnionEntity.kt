package ai.ivira.app.features.avasho.data.entity

data class AvashoArchiveUnionEntity(
    val id: Int,
    val fileUrl: String,
    val filePath: String,
    val fileName: String,
    val text: String,
    val createdAt: Long,
    val archiveType: String,
    val checksum: String,
    val speaker: String,
    val isDownloading: Boolean
) {
    fun toAvanegarProcessedFileEntity() = AvashoProcessedFileEntity(
        id = id,
        fileName = fileName,
        text = text,
        createdAt = createdAt,
        fileUrl = fileUrl,
        filePath = filePath,
        checksum = checksum,
        isDownloading = isDownloading
    )
}