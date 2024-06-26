package ai.ivira.app.features.ava_negar.ui.onboarding

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey.onBoardingKey
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    fun navigateArchiveListScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveOnBoardingState(completed = true, key = onBoardingKey)
        }

        shouldNavigate.value = true
    }
}