package ai.ivira.app.features.hamahang.ui.new_audio.sheets

import androidx.compose.runtime.saveable.Saver

sealed class HamahangNewAudioBottomSheetType() {
    data object UploadFile : HamahangNewAudioBottomSheetType()
    data object FileAccessPermissionDenied : HamahangNewAudioBottomSheetType()

    data class DeleteFileConfirmation(val fromUpload: Boolean) :
        HamahangNewAudioBottomSheetType()

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
                    UploadFile -> {
                        listOf(state::class.java.simpleName)
                    }
                }
            },
            restore = { list ->
                when (list.firstOrNull()) {
                    UploadFile::javaClass.name -> UploadFile
                    FileAccessPermissionDenied::javaClass.name -> FileAccessPermissionDenied
                    DeleteFileConfirmation::javaClass.name -> {
                        val fromUpload = list[1] as Boolean
                        DeleteFileConfirmation(fromUpload)
                    }
                    else -> null
                }
            }
        )
    }
}