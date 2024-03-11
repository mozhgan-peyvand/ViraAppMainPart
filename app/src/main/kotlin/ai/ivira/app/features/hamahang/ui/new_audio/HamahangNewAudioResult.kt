package ai.ivira.app.features.hamahang.ui.new_audio

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HamahangNewAudioResult(
    val inputPath: String,
    val speaker: HamahangSpeaker
) : Parcelable {
    companion object {
        const val NEW_FILE_AUDIO_RESULT = "newFileAudioResult"
    }
}