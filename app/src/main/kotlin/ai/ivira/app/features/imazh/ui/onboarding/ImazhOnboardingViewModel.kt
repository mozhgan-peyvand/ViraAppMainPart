package ai.ivira.app.features.imazh.ui.onboarding

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

const val IMAZH_ONBOARDING_COMPLETED = "imazh_onboarding_completed"

@HiltViewModel
class ImazhOnboardingViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    fun completeImazhOnBoarding() {
        viewModelScope.launch(IO) {
            sharedPref.edit().putBoolean(IMAZH_ONBOARDING_COMPLETED, true).apply()
        }
        shouldNavigate.value = true
    }
}