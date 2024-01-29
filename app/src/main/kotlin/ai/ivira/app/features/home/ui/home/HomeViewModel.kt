package ai.ivira.app.features.home.ui.home

import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey.onBoardingKey
import ai.ivira.app.features.avasho.ui.onboarding.AVASHO_ONBOARDING_COMPLETED
import ai.ivira.app.features.config.ui.TileItem
import ai.ivira.app.features.home.data.VersionRepository
import ai.ivira.app.features.home.ui.home.version.model.toVersionView
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.data.NetworkStatusTracker
import ai.ivira.app.utils.data.api_result.AppResult.Error
import ai.ivira.app.utils.data.api_result.AppResult.Success
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiException
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiStatus
import ai.ivira.app.utils.ui.UiSuccess
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CURRENT_TIME_PREF_KEY = "currentTimePrefKey"
private const val SHOWING_PERMISSION_REQUEST_INTERVAL = 7 * DateUtils.DAY_IN_MILLIS

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    repository: DataStoreRepository,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    val networkStatus = networkStatusTracker.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkStatus.Unavailable
    )

    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener
    val changeLogList = versionRepository.getChangeLogFromLocal()
        .map { changeLogList ->
            changeLogList.map { changeLogDto ->
                changeLogDto.toVersionView()
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    var shouldNavigate = mutableStateOf(false)
    var shouldNavigateToAvasho = mutableStateOf(false)
    var shouldShowNotificationBottomSheet = false
        private set

    private var _showUpdateBottomSheet = MutableStateFlow(false)
    val showUpdateBottomSheet = combine(
        _showUpdateBottomSheet,
        changeLogList
    ) { showUpdateBottomSheet, changeLogList ->
        showUpdateBottomSheet && changeLogList.isNotEmpty()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    var canShowBottomSheet = true
        private set

    var onboardingHasBeenShown = mutableStateOf(false)
        private set

    var avashoOnboardingHasBeenShown = mutableStateOf(false)
        private set

    val unavailableTileToShowBottomSheet: MutableState<TileItem?> = mutableStateOf(null)

    init {
        // region notification request
        val previousPermissionRequest = sharedPref.getLong(
            CURRENT_TIME_PREF_KEY,
            System.currentTimeMillis()
        )
        val hasEnoughTimePassed =
            previousPermissionRequest + SHOWING_PERMISSION_REQUEST_INTERVAL > System.currentTimeMillis()

        shouldShowNotificationBottomSheet = hasEnoughTimePassed
        // endregion

        viewModelScope.launch {
            repository.readOnBoardingState(onBoardingKey).collect { completed ->
                onboardingHasBeenShown.value = completed
            }
        }

        avashoOnboardingHasBeenShown.value = sharedPref.getBoolean(
            AVASHO_ONBOARDING_COMPLETED,
            false
        )

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AVASHO_ONBOARDING_COMPLETED) {
                avashoOnboardingHasBeenShown.value = sharedPref.getBoolean(
                    AVASHO_ONBOARDING_COMPLETED,
                    false
                )
            }
        }

        sharedPref.registerOnSharedPreferenceChangeListener(prefListener)

        _showUpdateBottomSheet.value = versionRepository.shouldShowBottomSheet()
    }

    fun showLater() {
        versionRepository.showUpdateBottomSheetLater()
    }

    fun doNotShowUpdateBottomSheetUntilNextLaunch() {
        canShowBottomSheet = false
    }

    fun navigate() {
        shouldNavigate.value = true
    }

    fun navigateToAvasho() {
        shouldNavigateToAvasho.value = true
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

    fun getUpdateList() {
        viewModelScope.launch(IO) {
            _uiViewState.update {
                UiLoading
            }

            when (val result = versionRepository.getChangeLogFromRemote()) {
                is Success -> {
                    _uiViewState.update {
                        UiSuccess
                    }
                }

                is Error -> {
                    _uiViewState.update {
                        UiError(uiException.getErrorMessage(result.error))
                    }
                }
            }
        }
    }

    fun clearUiState() {
        _uiViewState.value = UiIdle
    }

    override fun onCleared() {
        super.onCleared()
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}