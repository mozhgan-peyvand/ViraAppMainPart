package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate

data class HamahangTrackingFileView(
    val token: String,
    val title: String,
    val inputFilePath: String,
    val speaker: String,
    val processEstimation: Int?,
    val createdAt: String,
    val createdAtMillis: Long,
    val bootElapsedTime: Long,
    val lastFailure: Boolean
) : HamahangArchiveView {
    // CalculateEstimateProcess: Duplicate 4
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

fun HamahangTrackingFileEntity.toHamahangTrackingFileView() = HamahangTrackingFileView(
    token = token,
    title = title,
    processEstimation = processEstimation,
    inputFilePath = inputFilePath,
    speaker = speaker,
    createdAt = convertDate(insertAt.systemTime),
    createdAtMillis = insertAt.systemTime,
    bootElapsedTime = insertAt.bootTime,
    lastFailure = lastFailure != null
)