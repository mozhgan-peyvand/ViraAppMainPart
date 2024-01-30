package ai.ivira.app.features.avasho.ui.archive.model

import ai.ivira.app.features.ava_negar.ui.archive.model.convertDate
import ai.ivira.app.features.avasho.data.entity.AvashoProcessedFileEntity
import ai.ivira.app.utils.common.file.getFileDuration
import android.media.MediaMetadataRetriever
import java.io.File

data class AvashoProcessedFileView(
    val id: Int,
    val fileUrl: String,
    val filePath: String,
    override val title: String,
    val text: String,
    val createdAt: String,
    val fileSize: Long?,
    val downloadedBytes: Long?,
    val downloadingPercent: Float,
    val isDownloading: Boolean,
    val fileDuration: Long,
    val isSeen: Boolean
) : AvashoArchiveView

fun AvashoProcessedFileEntity.toAvashoProcessedFileView(
    downloadingId: Int = -1,
    downloadingPercent: Float = -1f,
    fileSize: Long?,
    downloadedBytes: Long?,
    retriever: MediaMetadataRetriever?
) = AvashoProcessedFileView(
    id = id,
    title = fileName,
    text = text,
    createdAt = convertDate(createdAt),
    fileUrl = fileUrl,
    filePath = filePath,
    isSeen = isSeen,
    fileSize = if (filePath.isNotEmpty()) File(filePath).length() else fileSize,
    downloadedBytes = downloadedBytes,
    downloadingPercent = if (downloadingId == id) downloadingPercent else -1f, // it's default and initial value
    isDownloading = isDownloading,
    fileDuration = if (retriever != null && File(filePath).exists()) {
        getFileDuration(
            retriever,
            filePath
        )
    } else {
        0L
    }
)