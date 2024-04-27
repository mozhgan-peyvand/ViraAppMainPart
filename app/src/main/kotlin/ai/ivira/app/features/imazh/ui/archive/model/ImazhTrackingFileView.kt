package ai.ivira.app.features.imazh.ui.archive.model

import ai.ivira.app.features.ava_negar.ui.archive.model.convertDate
import ai.ivira.app.features.imazh.data.entity.ImazhTrackingFileEntity
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate

data class ImazhTrackingFileView(
    val token: String,
    val keywords: List<String>,
    val prompt: String,
    val negativePrompt: String,
    val style: String,
    val processEstimation: Int?,
    val lastFailure: Boolean,
    val createdAt: String,
    val createdAtMillis: Long,
    val bootElapsedTime: Long
) : ImazhArchiveView {
    // CalculateEstimateProcess: Duplicate 3
    fun computeFileEstimateProcess(): Double {
        if (processEstimation == null) return -1.0
        if (lastFailure || processEstimation <= 0) return -1.0
        if (SystemClock.elapsedRealtime() > bootElapsedTime) {
            val diff = (SystemClock.elapsedRealtime() - bootElapsedTime) / 1000
            return (processEstimation - diff) * 1.2
        }
        if (PersianDate().time > createdAtMillis) {
            val diff = (PersianDate().time - createdAtMillis) / 1000
            return (processEstimation - diff) * 1.2
        }

        return -1.0
    }
}

fun ImazhTrackingFileEntity.toImazhTrackingFileView() = ImazhTrackingFileView(
    token = token,
    processEstimation = processEstimation,
    createdAt = convertDate(insertAt.systemTime),
    createdAtMillis = insertAt.systemTime,
    bootElapsedTime = insertAt.bootTime,
    keywords = keywords,
    prompt = prompt,
    negativePrompt = negativePrompt,
    style = style,
    lastFailure = lastFailure != null
)