package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.features.hamahang.ui.new_audio.HamahangNewAudioResult.Companion.NEW_FILE_AUDIO_RESULT
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HamahangNewAudioScreenRoute(navController: NavController) {
    HamahangNewAudioScreen(
        navigateUp = { result ->
            if (result != null) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        NEW_FILE_AUDIO_RESULT,
                        HamahangNewAudioResult(
                            inputPath = result.inputPath,
                            speaker = result.speaker
                        )
                    )
            }

            navController.popBackStack()
        }
    )
}

@Composable
private fun HamahangNewAudioScreen(
    navigateUp: (HamahangNewAudioResult?) -> Unit
) {
    HamahangNewAudioUI()
}

@Composable
private fun HamahangNewAudioUI() {
}