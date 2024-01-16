package ai.ivira.app.features.imazh.ui.newImageDescriptor.model

import ai.ivira.app.features.imazh.data.entity.ImazhKeywordEntity

data class ImazhKeywordView(
    val farsi: String,
    val english: String
) {
    fun toImazhKeywordEntity() = ImazhKeywordEntity(
        farsi = farsi,
        english = english
    )
}

fun ImazhKeywordEntity.toImazhKeywordView() = ImazhKeywordView(
    farsi = farsi,
    english = english
)