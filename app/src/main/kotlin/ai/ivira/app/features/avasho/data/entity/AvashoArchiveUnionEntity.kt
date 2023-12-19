package ai.ivira.app.features.avasho.data.entity

import ai.ivira.app.utils.data.TrackTime

data class AvashoArchiveUnionEntity(
    val id: Int,
    val uploadingId: String,
    val token: String,
    val text: String,
    val fileName: String,
    val insertSystemTime: Long,
    val insertBootTime: Long,
    val fileUrl: String,
    val filePath: String,
    val isDownloading: Boolean,
    val speaker: String,
    val archiveType: String,
    val processEstimation: Int?,
    val lastFailureSystemTime: Long?,
    val lastFailureBootTime: Long?
) {
    fun toAvanegarProcessedFileEntity() = AvashoProcessedFileEntity(
        id = id,
        fileName = fileName,
        text = text,
        createdAt = insertSystemTime,
        fileUrl = fileUrl,
        filePath = filePath,
        isDownloading = isDownloading
    )

    fun toAvashoTrackingFileEntity() = AvashoTrackingFileEntity(
        token = token,
        title = fileName,
        text = text,
        processEstimation = processEstimation,
        insertAt = TrackTime(insertSystemTime, insertBootTime),
        lastFailure = if (lastFailureSystemTime != null && lastFailureBootTime != null) {
            if (lastFailureBootTime != 0L && lastFailureSystemTime != 0L) {
                TrackTime(lastFailureSystemTime, lastFailureBootTime)
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
        createdAt = insertSystemTime
    )
}