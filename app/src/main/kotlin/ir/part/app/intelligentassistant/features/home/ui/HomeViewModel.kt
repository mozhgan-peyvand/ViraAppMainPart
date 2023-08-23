package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.features.ava_negar.data.DataStoreRepository
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject() constructor(
    repository: DataStoreRepository
) : ViewModel() {

    private val _startDestination: MutableState<String> =
        mutableStateOf(ScreenRoutes.AvaNegarOnboarding.route)

    val startDestination = _startDestination

    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value =
                        ScreenRoutes.AvaNegarArchiveList.route
                } else {
                    _startDestination.value =
                        ScreenRoutes.AvaNegarOnboarding.route
                }
            }
        }
    }
}