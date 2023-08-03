package ir.part.app.intelligentassistant.ui.screen.archive

sealed class UploadFileStatus

object UploadIdle : UploadFileStatus()

object UploadSuccess : UploadFileStatus()

object UploadFailure : UploadFileStatus()

object UploadInProgress : UploadFileStatus()