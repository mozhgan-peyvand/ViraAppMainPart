package ir.part.app.intelligentassistant.features.ava_negar.ui.record

sealed interface VoiceRecordingViewState {
    object Idle : VoiceRecordingViewState
    data class Recording(val hasPaused: Boolean) : VoiceRecordingViewState
    object Paused : VoiceRecordingViewState
    object Stopped : VoiceRecordingViewState

    fun serialize(): String {
        return when (this) {
            is Recording -> "recording:$hasPaused"
            Idle -> "idle"
            Paused -> "paused"
            Stopped -> "stopped"
        }
    }

    companion object {
        fun deserialize(str: String): VoiceRecordingViewState {
            return if (str == "idle") {
                Idle
            } else if (str == "paused") {
                Paused
            } else if (str == "stopped") {
                Stopped
            } else {
                Recording(str.replace("recording:", "").toBoolean())
            }
        }
    }
}