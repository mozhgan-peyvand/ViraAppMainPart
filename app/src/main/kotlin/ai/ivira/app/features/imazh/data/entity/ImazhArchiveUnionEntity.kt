package ai.ivira.app.features.imazh.data.entity

import ai.ivira.app.utils.data.TrackTime

data class ImazhArchiveUnionEntity(
    val id: Int,
    val token: String,
    val archiveType: String,
    val imagePath: String,
    val filePath: String,
    val keywords: List<String>,
    val englishKeywords: List<String>,
    val prompt: String,
    val englishPrompt: String,
    val style: String,
    val insertBootTime: Long,
    val insertSystemTime: Long,
    val lastFailureSystemTime: Long?,
    val lastFailureBootTime: Long?,
    val processEstimation: Int?,
    val nsfw: Boolean
) {
    fun toImazhTrackingFileEntity() = ImazhTrackingFileEntity(
        token = token,
        keywords = keywords,
        englishKeywords = englishKeywords,
        prompt = prompt,
        englishPrompt = englishPrompt,
        style = style,
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

    fun toImazhProcessedFileEntity() = ImazhProcessedFileEntity(
        id = id,
        imagePath = imagePath,
        filePath = filePath,
        keywords = keywords,
        englishKeywords = englishKeywords,
        prompt = prompt,
        englishPrompt = englishPrompt,
        style = style,
        createdAt = insertSystemTime,
        nsfw = nsfw
    )
}