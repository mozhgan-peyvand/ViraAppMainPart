package ai.ivira.app.features.ava_negar.data.entity

import ai.ivira.app.utils.data.TrackTime

data class AvanegarArchiveUnionEntity(
    val id: Int,
    val uploadingId: String,
    val title: String,
    val text: String,
    val insertSystemTime: Long,
    val filePath: String,
    val token: String,
    val fileDuration: Long,
    val isSeen: Boolean,
    val archiveType: String,
    val insertBootTime: Long,
    val processEstimation: Int?,
    val lastFailureSystemTime: Long?,
    val lastFailureBootTime: Long?
) {
    fun toAvanegarTrackingFileEntity() = AvanegarTrackingFileEntity(
        token = token,
        filePath = filePath,
        title = title,
        insertAt = TrackTime(insertSystemTime, insertBootTime),
        processEstimation = processEstimation,
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

    fun toAvanegarProcessedFileEntity() = AvanegarProcessedFileEntity(
        id = id,
        title = title,
        text = text,
        createdAt = insertSystemTime,
        filePath = filePath,
        isSeen = isSeen
    )

    fun toAvanegarUploadingFileEntity() = AvanegarUploadingFileEntity(
        title = title,
        id = uploadingId,
        filePath = filePath,
        createdAt = insertSystemTime,
        fileDuration = fileDuration
    )
}