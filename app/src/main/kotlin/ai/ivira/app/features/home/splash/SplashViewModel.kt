package ai.ivira.app.features.home.splash

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey.mainOnBoardingKey
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: DataStoreRepository
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
        private set

    var hasOnboardingShown = mutableStateOf(false)
        private set

    init {

        viewModelScope.launch {
            repository.readOnBoardingState(mainOnBoardingKey)
                .stateIn(scope = viewModelScope)
                .collect {
                    hasOnboardingShown.value = it
                }
        }
    }

    fun navigateToMainOnboarding() {
        shouldNavigate.value = true
    }
}