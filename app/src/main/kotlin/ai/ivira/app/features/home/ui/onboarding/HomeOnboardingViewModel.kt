package ai.ivira.app.features.home.ui.onboarding

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeOnboardingViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    fun onBoardingShown() {
        viewModelScope.launch(IO) {
            repository.saveOnBoardingState(
                completed = true,
                key = PreferencesKey.mainOnBoardingKey
            )
        }
    }

    fun navigateToHomeScreen() {
        shouldNavigate.value = true
    }
}