package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.features.hamahang.ui.archive.model.HamahangSpeakerView
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HamahangNewAudioResult(
    val title: String,
    val inputPath: String,
    val speaker: HamahangSpeakerView
) : Parcelable {
    companion object {
        const val NEW_FILE_AUDIO_RESULT = "newFileAudioResult"
    }
}