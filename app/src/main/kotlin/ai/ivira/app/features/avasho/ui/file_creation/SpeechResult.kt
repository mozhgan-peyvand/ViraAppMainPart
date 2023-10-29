package ai.ivira.app.features.avasho.ui.file_creation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpeechResult(
    val fileName: String,
    val text: String,
    val speakerType: String
) : Parcelable {
    companion object {
        const val FILE_NAME = "convertTextToSpeech"
    }
}