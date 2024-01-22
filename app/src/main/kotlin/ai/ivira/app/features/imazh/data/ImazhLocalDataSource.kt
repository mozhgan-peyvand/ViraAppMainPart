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
        imagePath: String,
        createdAt: Long,
        filePath: String
    ) = dao.insertPhotoInfo(
        ImazhProcessedEntity(
            id = 0,
            imagePath = imagePath,
            filePath = filePath,
            keywords = keywords,
            prompt = prompt,
            negativePrompt = negativePrompt,
            style = style,
            createdAt = createdAt
        )
    )

    fun getAllProcessedFiles() = dao.getAllProcessedFiles()

    fun getPhotoInfo(id: Int) = dao.getPhotoInfo(id)

    suspend fun deletePhotoInfo(id: Int) = dao.deletePhotoInfo(id)

    suspend fun updateFilePath(id: Int, filePath: String) {
        dao.updateFilePath(id, filePath)
    }
}