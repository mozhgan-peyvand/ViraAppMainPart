package ir.part.app.intelligentassistant.ui.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.part.app.intelligentassistant.data.DataStoreRepository
import ir.part.app.intelligentassistant.ui.navigation.ScreensRouter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject() constructor(
    repository: DataStoreRepository
) : ViewModel() {

    private val _startDestination: MutableState<String> =
        mutableStateOf(ScreensRouter.AvaNegarOnBoardingScreen.router)

    val startDestination = _startDestination

    init {
        viewModelScope.launch {
            repository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value =
                        ScreensRouter.AvaNegarArchiveScreen.router
                } else {
                    _startDestination.value =
                        ScreensRouter.AvaNegarOnBoardingScreen.router
                }
            }
        }
    }
}