package ai.ivira.app.features.ava_negar.ui.archive.model

import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import android.os.SystemClock
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

data class AvanegarTrackingFileView(
    val token: String,
    val filePath: String,
    override val title: String,
    val processEstimation: Int?,
    val createdAt: String,
    val bootElapsedTime: Long,
    val lastFailure: Boolean
) : ArchiveView {
    fun computeFileEstimateProcess(): Double {
        if (processEstimation == null) return -1.0
        if (lastFailure || processEstimation <= 0) return -1.0
        if (SystemClock.elapsedRealtime() > bootElapsedTime) {
            val diff = (SystemClock.elapsedRealtime() - bootElapsedTime) / 1000
            return (processEstimation - diff) * ESTIMATED_TIME_FACTOR
        } else if (PersianDate().time > createdAt.toLong()) {
            val diff = (PersianDate().time - createdAt.toLong()) / 1000
            return (processEstimation - diff) * ESTIMATED_TIME_FACTOR
        }

        return (-1).toDouble()
    }

    companion object {
        private const val ESTIMATED_TIME_FACTOR = 1.1
    }
}

fun AvanegarTrackingFileEntity.toAvanegarTrackingFileView() = AvanegarTrackingFileView(
    token = token,
    filePath = filePath,
    title = title,
    processEstimation = processEstimation,
    createdAt = convertDate(insertAt.systemTime),
    bootElapsedTime = insertAt.bootTime,
    lastFailure = lastFailure != null
)

fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}