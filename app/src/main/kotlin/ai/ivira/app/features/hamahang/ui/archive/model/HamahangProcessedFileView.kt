package ai.ivira.app.features.hamahang.ui.archive.model

import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangSpeakerView
import ai.ivira.app.utils.common.file.getFileDuration
import android.media.MediaMetadataRetriever
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.io.File

data class HamahangProcessedFileView(
    val id: Int,
    val title: String,
    val fileUrl: String,
    val filePath: String,
    val inputFilePath: String,
    val speaker: HamahangSpeakerView,
    val createdAt: String,
    val isSeen: Boolean,
    val downloadedBytes: Long?,
    val downloadingPercent: Float,
    val fileDuration: Long
) : HamahangArchiveView

fun HamahangProcessedFileEntity.toHamahangProcessedFileView(
    downloadingId: Int = -1,
    downloadingPercent: Float = -1f,
    downloadedBytes: Long? = null,
    retriever: MediaMetadataRetriever? = null
) = HamahangProcessedFileView(
    id = id,
    title = title,
    fileUrl = fileUrl,
    filePath = filePath,
    inputFilePath = inputFilePath,
    speaker = HamahangSpeakerView.findByName(speaker),
    createdAt = convertDate(createdAt),
    isSeen = isSeen,
    downloadedBytes = downloadedBytes,
    downloadingPercent = if (downloadingId == id) downloadingPercent else -1f, // it's default and initial value,
    fileDuration = if (retriever != null && File(filePath).exists()) {
        getFileDuration(
            retriever,
            filePath
        )
    } else {
        0L
    }
)

// duplicate 3
fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}