package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.features.home.data.entity.VersionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VersionLocalDataSource @Inject constructor(
    private val dao: VersionDao
) {
    suspend fun insertChangeLog(list: List<VersionEntity>) {
        dao.insertChangeLog(list)
    }

    suspend fun insertReleaseNote(list: List<ReleaseNoteEntity>) {
        dao.insertReleaseNote(list)
    }

    suspend fun deleteReleaseNote() {
        dao.deleteReleaseNote()
    }

    fun getChangeLog(): Flow<List<VersionDto>> = dao.getChangeLog()
}