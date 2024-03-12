package ai.ivira.app.features.imazh.ui.archive.model

import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedFileEntity
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.io.File

data class ImazhProcessedFileView(
    val id: Int,
    val imagePath: String,
    val filePath: String,
    val keywords: List<String>,
    val prompt: String,
    val style: ImazhImageStyle,
    val createdAt: String,
    val fileSize: Long?,
    val downloadedBytes: Long?,
    val downloadingPercent: Float
) : ImazhArchiveView

fun ImazhProcessedFileEntity.toImazhProcessedFileView(
    downloadingId: Int = -1,
    downloadingPercent: Float = -1f,
    fileSize: Long? = null,
    downloadedBytes: Long? = null
) = ImazhProcessedFileView(
    id = id,
    imagePath = imagePath,
    filePath = filePath,
    keywords = keywords,
    prompt = prompt,
    style = ImazhImageStyle.findByKey(style),
    createdAt = convertDate(createdAt),
    fileSize = if (filePath.isNotEmpty()) File(filePath).length() else fileSize,
    downloadedBytes = downloadedBytes,
    downloadingPercent = if (downloadingId == id) downloadingPercent else -1f
)

// duplicate 2
fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}