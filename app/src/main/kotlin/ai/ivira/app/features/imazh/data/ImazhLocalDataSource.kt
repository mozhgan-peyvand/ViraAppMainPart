package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import ai.ivira.app.features.imazh.data.entity.ImazhProcessedEntity
import javax.inject.Inject

class ImazhLocalDataSource @Inject constructor(
    private val dao: ImazhDao
) {
    fun getRecentHistory() = dao.getRecentHistory()

    suspend fun addPromptToHistory(item: ImazhHistoryEntity) {
        dao.addPromptToHistory(item)
    }

    suspend fun addImageToDataBase(
        prompt: String,
        negativePrompt: String,
        keywords: List<String>,
        style: String,
        imagePath: String
    ) = dao.insertPhotoInfo(
        ImazhProcessedEntity(
            id = 0,
            imagePath = imagePath,
            keywords = keywords,
            prompt = prompt,
            negativePrompt = negativePrompt,
            style = style
        )
    )

    fun getAllProcessedFiles() = dao.getAllProcessedFiles()
}