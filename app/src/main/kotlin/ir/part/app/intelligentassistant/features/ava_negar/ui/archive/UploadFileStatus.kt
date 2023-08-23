package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

sealed class UploadFileStatus

object UploadIdle : UploadFileStatus()

object UploadSuccess : UploadFileStatus()

object UploadFailure : UploadFileStatus()

object UploadInProgress : UploadFileStatus()