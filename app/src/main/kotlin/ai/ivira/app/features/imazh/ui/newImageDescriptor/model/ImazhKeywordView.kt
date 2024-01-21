package ai.ivira.app.features.imazh.ui.newImageDescriptor.model

import ai.ivira.app.features.imazh.data.entity.ImazhKeywordEntity

data class ImazhKeywordView(
    val keywordName: String,
    val farsiKeyword: String,
    val englishKeyword: String
) {
    fun toImazhKeywordEntity() = ImazhKeywordEntity(
        keywordName = keywordName,
        farsiKeyword = farsiKeyword,
        englishKeyword = englishKeyword
    )
}

fun ImazhKeywordEntity.toImazhKeywordView() = ImazhKeywordView(
    keywordName = keywordName,
    farsiKeyword = farsiKeyword,
    englishKeyword = englishKeyword
)