package ai.ivira.app.features.hamahang.data

import ai.ivira.app.features.hamahang.data.entity.HamahangProcessedFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangTrackingFileEntity
import ai.ivira.app.features.hamahang.data.entity.HamahangUploadingFileEntity
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangSpeaker
import ai.ivira.app.utils.data.TrackTime
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HamahangFakeData @Inject constructor(
    @ApplicationContext context: Context
) {
    private val assetFilPath = "tmp/1.mp3" // TODO: upon removing fake data, remove this file as well
    private val internalFile = File(File(context.filesDir, "hamahang"), "1.mp3")

    init {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            delay(100)
            if (!internalFile.exists()) {
                val stream = context.assets.open(assetFilPath)
                internalFile.parentFile?.mkdirs()
                val os = FileOutputStream(internalFile, false)
                val bytes = ByteArray(1024)

                while (true) {
                    val c = stream.read(bytes)
                    if (c == -1) break
                    os.write(bytes, 0, c)
                }

                os.close()
                stream.close()
            }
        }
    }

    val processedFiles = listOf(
        HamahangProcessedFileEntity(
            id = 1,
            title = "صوت ۱",
            fileUrl = "/service/voiceConversion/135531d6e06911eead3c0242ac110003.mp3",
            filePath = internalFile.absolutePath,
            inputFilePath = internalFile.absolutePath,
            speaker = HamahangSpeaker.KHIABANI.name,
            createdAt = PersianDate().apply { hour -= 1 }.time,
            isSeen = false
        ),
        HamahangProcessedFileEntity(
            id = 2,
            title = "صوت 2",
            fileUrl = "/service/voiceConversion/135531d6e06911eead3c0242ac110003.mp3",
            filePath = internalFile.absolutePath,
            inputFilePath = internalFile.absolutePath,
            speaker = HamahangSpeaker.KHIABANI.name,
            createdAt = PersianDate().time,
            isSeen = true
        )
    )

    val trackingFiles = listOf(
        HamahangTrackingFileEntity(
            token = UUID.randomUUID().toString(),
            title = "صدای استاد 1",
            inputFilePath = internalFile.absolutePath,
            speaker = HamahangSpeaker.KHIABANI.name,
            processEstimation = 450,
            insertAt = TrackTime(
                systemTime = System.currentTimeMillis(),
                bootTime = SystemClock.elapsedRealtime()
            ),
            lastFailure = null
        ),
        HamahangTrackingFileEntity(
            token = UUID.randomUUID().toString(),
            title = "صدای استاد 2",
            inputFilePath = internalFile.absolutePath,
            speaker = HamahangSpeaker.KHIABANI.name,
            processEstimation = null,
            insertAt = TrackTime(
                systemTime = PersianDate().time,
                bootTime = SystemClock.elapsedRealtime()
            ),
            lastFailure = null
        )
    )

    val uploadingFiles = listOf(
        HamahangUploadingFileEntity(
            id = "per",
            title = "صدای چاووشی",
            inputFilePath = internalFile.absolutePath,
            speaker = HamahangSpeaker.KHIABANI.name,
            createdAt = PersianDate().time

        )
    )
}