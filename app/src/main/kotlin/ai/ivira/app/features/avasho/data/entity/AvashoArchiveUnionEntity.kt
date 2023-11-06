package ai.ivira.app.features.avasho.data.entity

data class AvashoArchiveUnionEntity(
    val id: Int,
    val uploadingId: String,
    val token: String,
    val text: String,
    val fileName: String,
    val createdAt: Long,
    val fileUrl: String,
    val filePath: String,
    val checksum: String,
    val isDownloading: Boolean,
    val speaker: String,
    val archiveType: String,
    val processEstimation: Int?,
    val bootElapsedTime: Long,
    val lastFailedRequest: Long?,
    val lastTrackedBootElapsed: Long?
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

    fun toAvashoTrackingFileEntity() = AvashoTrackingFileEntity(
        token = token,
        title = fileName,
        createdAt = createdAt,
        processEstimation = processEstimation,
        bootElapsedTime = bootElapsedTime,
        lastFailure = if (lastFailedRequest != null && lastTrackedBootElapsed != null) {
            if (lastTrackedBootElapsed != 0L && lastFailedRequest != 0L) {
                AvashoLastTrackFailure(lastFailedRequest, lastTrackedBootElapsed)
            } else {
                null
            }
        } else {
            null
        }
    )

    fun toAvashoUploadingFileEntity() = AvashoUploadingFileEntity(
        id = uploadingId,
        title = fileName,
        text = text,
        speaker = speaker,
        createdAt = createdAt
    )
}