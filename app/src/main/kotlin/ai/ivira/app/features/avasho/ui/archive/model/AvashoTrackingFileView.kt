package ai.ivira.app.features.avasho.ui.archive.model

import ai.ivira.app.features.ava_negar.ui.archive.model.convertDate
import ai.ivira.app.features.avasho.data.entity.AvashoTrackingFileEntity
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate

data class AvashoTrackingFileView(
    val token: String,
    override val title: String,
    val processEstimation: Int?,
    val createdAt: String,
    val createdAtMillis: Long,
    val bootElapsedTime: Long,
    val lastFailure: Boolean
) : AvashoArchiveView {
    // CalculateEstimateProcess: Duplicate 2
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

fun AvashoTrackingFileEntity.toAvashoTrackingFileView() = AvashoTrackingFileView(
    token = token,
    title = title,
    processEstimation = processEstimation,
    createdAt = convertDate(insertAt.systemTime),
    createdAtMillis = insertAt.systemTime,
    bootElapsedTime = insertAt.bootTime,
    lastFailure = lastFailure != null
)