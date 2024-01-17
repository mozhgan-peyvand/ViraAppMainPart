package ai.ivira.app.features.imazh.ui.archive.model

import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

data class ImazhProcessedFileView(
    val id: Int,
    val imagePath: String,
    val filePath: String,
    val keywords: List<String>,
    val prompt: String,
    val negativePrompt: String,
    val style: ImazhImageStyle,
    val createdAt: String
) : ImazhArchiveView

fun ImazhProcessedEntity.toImazhProcessedFileView() = ImazhProcessedFileView(
    id = id,
    imagePath = imagePath,
    filePath = filePath,
    keywords = keywords,
    prompt = prompt,
    negativePrompt = negativePrompt,
    style = ImazhImageStyle.findByKey(style),
    createdAt = convertDate(createdAt)
)

// duplicate 2
fun convertDate(date: Long): String {
    return try {
        PersianDateFormat("Y/m/d").format(PersianDate(date))
    } catch (e: Exception) {
        ""
    }
}