package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import javax.inject.Inject

class ImazhLocalDataSource @Inject constructor(
    private val dao: ImazhDao
) {
    fun getRecentHistory() = dao.getRecentHistory()

    suspend fun insertProcessed(list: List<ImazhHistoryEntity>) {
        dao.insertProcessed(list)
    }
}