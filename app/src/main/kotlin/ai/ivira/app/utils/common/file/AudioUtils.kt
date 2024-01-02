package ai.ivira.app.utils.common.file

import ai.ivira.app.utils.common.orZero
import android.media.MediaMetadataRetriever
import androidx.annotation.WorkerThread

@WorkerThread
fun getFileDuration(
    retriever: MediaMetadataRetriever,
    filePath: String
): Long {
    return runCatching {
        retriever.setDataSource(filePath)
        val time: String? =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        time?.toLong()
    }.getOrNull().orZero()
}