package ai.ivira.app.features.ava_negar.ui.record

sealed interface VoiceRecordingViewState {
    data object Idle : VoiceRecordingViewState
    data class Recording(val hasPaused: Boolean) : VoiceRecordingViewState
    data object Paused : VoiceRecordingViewState
    data class Stopped(val showPreview: Boolean) : VoiceRecordingViewState
}