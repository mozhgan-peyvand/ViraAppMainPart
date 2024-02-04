package ai.ivira.app.features.home.data

import ai.ivira.app.features.home.data.entity.ReleaseNoteEntity
import ai.ivira.app.features.home.data.entity.VersionDto
import ai.ivira.app.features.home.data.entity.VersionEntity
import ai.ivira.app.utils.data.db.ViraDb
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VersionLocalDataSource @Inject constructor(
    private val db: ViraDb,
    private val dao: VersionDao
) {
    suspend fun insertChangeLog(
        versions: List<VersionEntity>,
        releaseNotes: List<ReleaseNoteEntity>
    ) {
        db.withTransaction {
            dao.deleteVersions()
            dao.insertVersions(versions)

            dao.deleteReleaseNote()
            dao.insertReleaseNote(releaseNotes)
        }
    }

    fun getChangeLog(): Flow<List<VersionDto>> = dao.getChangeLog()
}