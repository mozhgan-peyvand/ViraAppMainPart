package ai.ivira.app.features.hamahang.ui.new_audio.sheets

import androidx.compose.runtime.saveable.Saver

sealed class HamahangNewAudioBottomSheetType {
    data object UploadFile : HamahangNewAudioBottomSheetType()
    data object FileAccessPermissionDenied : HamahangNewAudioBottomSheetType()
    data object AudioAccessPermissionDenied : HamahangNewAudioBottomSheetType()
    data object MicrophoneIsBeingUsedAlready : HamahangNewAudioBottomSheetType()
    data class DeleteFileConfirmation(val fromUpload: Boolean) : HamahangNewAudioBottomSheetType()

    companion object {
        fun Saver() = Saver<HamahangNewAudioBottomSheetType, List<Any>>(
            save = { state ->
                when (state) {
                    is DeleteFileConfirmation -> {
                        listOf(
                            state::class.java.simpleName,
                            state.fromUpload
                        )
                    }
                    FileAccessPermissionDenied,
                    AudioAccessPermissionDenied,
                    MicrophoneIsBeingUsedAlready,
                    UploadFile -> {
                        listOf(state::class.java.simpleName)
                    }
                }
            },
            restore = { list ->
                when (list[0]) {
                    UploadFile::class.java.simpleName -> UploadFile
                    FileAccessPermissionDenied::class.java.simpleName -> FileAccessPermissionDenied
                    AudioAccessPermissionDenied::class.java.simpleName -> AudioAccessPermissionDenied
                    MicrophoneIsBeingUsedAlready::class.java.simpleName -> MicrophoneIsBeingUsedAlready
                    DeleteFileConfirmation::class.java.simpleName -> {
                        val fromUpload = list[1] as Boolean
                        DeleteFileConfirmation(fromUpload = fromUpload)
                    }
                    else -> throw IllegalArgumentException("unknown: $list")
                }
            }
        )
    }
}