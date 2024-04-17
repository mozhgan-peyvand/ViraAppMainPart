package ai.ivira.app.features.hamahang.ui.onboarding

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val HAMAHANG_ONBOARDING_COMPLETED = "hamahang_onboarding_completed"

@HiltViewModel
class HamahangOnbordingViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    fun completeImazhOnBoarding() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPref.edit().putBoolean(HAMAHANG_ONBOARDING_COMPLETED, true).apply()
        }
        shouldNavigate.value = true
    }
}