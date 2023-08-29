package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.features.ava_negar.data.DataStoreRepository
import ir.part.app.intelligentassistant.features.ava_negar.data.PreferencesKey.onBoardingKey
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject() constructor(
    repository: DataStoreRepository
) : ViewModel() {

    var shouldNavigate = mutableStateOf(false)

    var onboardingHasBeenShown = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repository.readOnBoardingState(onBoardingKey).collect { completed ->
                onboardingHasBeenShown.value = completed
            }
        }
    }

    fun navigate() {
        shouldNavigate.value = true
    }
}