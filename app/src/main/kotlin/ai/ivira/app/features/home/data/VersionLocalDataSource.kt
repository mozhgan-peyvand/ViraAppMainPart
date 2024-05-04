package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.VersionDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VersionLocalDataSource @Inject constructor(
    private val dao: VersionDao
) {
    fun getChangeLog(): Flow<List<VersionDto>> = dao.getChangeLog()
}