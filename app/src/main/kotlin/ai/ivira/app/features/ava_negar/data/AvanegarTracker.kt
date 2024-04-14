package ai.ivira.app.features.ava_negar.data

import ai.ivira.app.features.ava_negar.data.entity.AvanegarTrackingFileEntity
import ai.ivira.app.utils.data.tracker.Tracker
import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvanegarTracker @Inject constructor(
    private val avanegarRepository: AvanegarRepository,
    @ApplicationContext context: Context
) : Tracker<AvanegarTrackingFileEntity>(NO_ESTIMATE_DELAY_MS, TRACK_DELAY_AFTER_FAILURE_MS) {
    companion object {
        private const val TAG = "AvanegarTracker"
        private const val NO_ESTIMATE_DELAY_MS = 30 * DateUtils.SECOND_IN_MILLIS
        private const val TRACK_DELAY_AFTER_FAILURE_MS = 15 * DateUtils.SECOND_IN_MILLIS
    }

    override val items = avanegarRepository.getTrackingFiles()

    override suspend fun track(item: AvanegarTrackingFileEntity): Boolean {
        return avanegarRepository.trackLargeFileResult(item.token).isSuccess
    }

    init {
        removeUnusedFiles(context)
    }

    private fun removeUnusedFiles(context: Context) {
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            val files = avanegarRepository.getAllFilePaths().associateBy { it.filePath }
            File(context.filesDir, "avanegar").walkTopDown().filter {
                it.isFile && it.extension == "mp3" && !files.contains(it.absolutePath)
            }.forEach {
                kotlin.runCatching {
                    it.delete()
                }
            }
        }
    }
}