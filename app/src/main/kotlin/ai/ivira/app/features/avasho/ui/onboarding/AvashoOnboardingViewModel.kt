package ai.ivira.app.features.avasho.ui.onboarding

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

const val AVASHO_ONBOARDING_COMPLETED = "avasho_onBoarding_completed"

@HiltViewModel
class AvashoOnboardingViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    fun navigateToArchiveListScreen() {
        viewModelScope.launch(IO) {
            sharedPref.edit().putBoolean(AVASHO_ONBOARDING_COMPLETED, true).apply()
        }

        shouldNavigate.value = true
    }
}