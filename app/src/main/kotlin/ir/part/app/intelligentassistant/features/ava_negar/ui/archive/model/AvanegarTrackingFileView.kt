package ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model

import ir.part.app.intelligentassistant.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

data class AvanegarTrackingFileView(
    val token: String,
    val filePath: String,
    override val title: String,
    val createdAt: String
) : ArchiveView

fun AvanegarTrackingFileEntity.toAvanegarTrackingFileView() = AvanegarTrackingFileView(
    token = token,
    filePath = filePath,
    title = title,
    createdAt = convertDate(createdAt)
)

fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}