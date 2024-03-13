package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangSpeakerView
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

data class HamahangProcessedFileView(
    val id: Int,
    val title: String,
    val fileUrl: String,
    val filePath: String,
    val inputFilePath: String,
    val speaker: HamahangSpeakerView,
    val createdAt: String,
    val isSeen: Boolean
)

fun HamahangProcessedFileEntity.toHamahangProcessedFileView() = HamahangProcessedFileView(
    id = id,
    title = title,
    fileUrl = fileUrl,
    filePath = filePath,
    inputFilePath = inputFilePath,
    speaker = HamahangSpeakerView.findByName(speaker),
    createdAt = convertDate(createdAt),
    isSeen = isSeen
)

// duplicate 3
fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}