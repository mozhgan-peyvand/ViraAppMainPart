package ir.part.app.intelligentassistant.features.ava_negar.ui.splash

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.features.ava_negar.data.DataStoreRepository
import ir.part.app.intelligentassistant.features.ava_negar.data.PreferencesKey.mainOnBoardingKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: DataStoreRepository,

    ) : ViewModel() {

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

    //TODO move it to mainOnBoarding viewModel after it has been implemented
    fun onBoardingShown() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveOnBoardingState(completed = true, key = mainOnBoardingKey)
        }
    }
}