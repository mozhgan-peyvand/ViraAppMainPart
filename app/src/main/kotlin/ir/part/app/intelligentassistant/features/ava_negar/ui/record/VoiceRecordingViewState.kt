package ir.part.app.intelligentassistant.features.ava_negar.ui.record

sealed interface VoiceRecordingViewState {
    object Idle : VoiceRecordingViewState
    data class Recording(val hasPaused: Boolean) : VoiceRecordingViewState
    object Paused : VoiceRecordingViewState
    object Stopped : VoiceRecordingViewState
}