package ai.ivira.app.features.imazh.data

import ai.ivira.app.features.imazh.data.entity.ImazhHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImazhRepository @Inject constructor(
    private val localDataSource: ImazhLocalDataSource
) {
    fun getRecentHistory(): Flow<List<ImazhHistoryEntity>> = localDataSource.getRecentHistory()
}