package ai.ivira.app.features.avasho.ui.detail

import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AvashoDetailsBottomSheetViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val mediaPlayer = MediaPlayer()
    val playerState = VoicePlayerState(mediaPlayer, application)

    override fun onCleared() {
        super.onCleared()
        playerState.clear()
    }
}