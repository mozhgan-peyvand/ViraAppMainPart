package ai.ivira.app.features.ava_negar.ui.record

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordFileResult(
    val title: String,
    val filepath: String
) : Parcelable {
    companion object {
        const val FILE_NAME = "acceptedFileName"
    }
}