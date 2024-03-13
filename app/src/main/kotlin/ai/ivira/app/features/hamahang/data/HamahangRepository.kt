package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangArchiveFilesEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.utils.ui.combine
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HamahangRepository @Inject constructor(
    fakeData: HamahangFakeData
) {
    private val processedFiles = MutableStateFlow(fakeData.processedFiles)
    private val trackingFiles = MutableStateFlow(fakeData.trackingFiles)
    private val uploadingFiles = MutableStateFlow(fakeData.uploadingFiles)

    fun getArchiveFile(id: Int): Flow<HamahangProcessedFileEntity?> {
        val flow = MutableStateFlow(processedFiles.value.find { it.id == id })
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            processedFiles.collect { flow.value = it.find { it.id == id } }
        }
        return flow.asStateFlow()
    }

    fun getArchiveFiles(): Flow<HamahangArchiveFilesEntity> {
        return combine(processedFiles, trackingFiles, uploadingFiles) { a, b, c ->
            HamahangArchiveFilesEntity(
                processed = a,
                tracking = b,
                uploading = c
            )
        }
    }

    suspend fun deleteProcessedFile(id: Int) {
        // todo delete from database and fileStorage
        delay(50)
        processedFiles.value = processedFiles.value.filter { it.id != id }.toMutableList()
    }

    suspend fun deleteTrackingFile(token: String) {
        delay(50)
        trackingFiles.value = trackingFiles.value.filter { it.token != token }.toMutableList()
    }

    suspend fun deleteUploadingFile(id: String) {
        delay(50)
        uploadingFiles.value = uploadingFiles.value.filter { it.id != id }.toMutableList()
    }
}