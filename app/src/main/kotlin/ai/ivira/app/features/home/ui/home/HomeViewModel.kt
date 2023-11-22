package ai.ivira.app.features.home.ui.home

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey.onBoardingKey
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CURRENT_TIME_PREF_KEY = "currentTimePrefKey"
private const val SHOWING_PERMISSION_REQUEST_INTERVAL = 7 * DateUtils.DAY_IN_MILLIS

@HiltViewModel
class HomeViewModel @Inject() constructor(
    private val sharedPref: SharedPreferences,
    repository: DataStoreRepository
) : ViewModel() {
    var shouldNavigate = mutableStateOf(false)
    var shouldShowNotificationBottomSheet = false
        private set

    var onboardingHasBeenShown = mutableStateOf(false)
        private set

    init {
        val previousPermissionRequest = sharedPref.getLong(
            CURRENT_TIME_PREF_KEY,
            System.currentTimeMillis()
        )
        val hasEnoughTimePassed =
            previousPermissionRequest + SHOWING_PERMISSION_REQUEST_INTERVAL > System.currentTimeMillis()

        shouldShowNotificationBottomSheet = hasEnoughTimePassed

        viewModelScope.launch {
            repository.readOnBoardingState(onBoardingKey).collect { completed ->
                onboardingHasBeenShown.value = completed
            }
        }
    }

    fun navigate() {
        shouldNavigate.value = true
    }

    fun putDeniedPermissionToSharedPref(permission: String, deniedPermanently: Boolean) {
        viewModelScope.launch {
            sharedPref.edit {
                this.putBoolean(permissionDeniedPrefKey(permission), deniedPermanently)
            }
        }
    }

    fun hasDeniedPermissionPermanently(permission: String): Boolean {
        return sharedPref.getBoolean(permissionDeniedPrefKey(permission), false)
    }

    fun doNotShowUtilNextLaunch() {
        shouldShowNotificationBottomSheet = false
    }

    fun putCurrentTimeDayToSharedPref() {
        viewModelScope.launch {
            sharedPref.edit {
                this.putLong(CURRENT_TIME_PREF_KEY, System.currentTimeMillis())
            }
        }
    }

    private fun permissionDeniedPrefKey(permission: String): String {
        return "deniedPermission_$permission"
    }
}