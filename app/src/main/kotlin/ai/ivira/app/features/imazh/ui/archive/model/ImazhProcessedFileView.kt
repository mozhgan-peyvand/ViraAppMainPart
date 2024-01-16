package ai.ivira.app.features.imazh.ui.archive.model

import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity

data class ImazhProcessedFileView(
    val id: Int,
    val imagePath: String,
    val keywords: List<String>,
    val prompt: String,
    val negativePrompt: String,
    val style: String
) : ImazhArchiveView

fun ImazhProcessedEntity.toImazhProcessedFileView() = ImazhProcessedFileView(
    id = id,
    imagePath = imagePath,
    keywords = keywords,
    prompt = prompt,
    negativePrompt = negativePrompt,
    style = style
)