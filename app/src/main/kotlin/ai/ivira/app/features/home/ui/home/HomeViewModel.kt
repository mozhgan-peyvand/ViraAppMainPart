package ai.ivira.app.features.home.ui.home

import ai.ivira.app.BuildConfig
import ai.ivira.app.features.ava_negar.data.DataStoreRepository
import ai.ivira.app.features.ava_negar.data.PreferencesKey.onBoardingKey
import ai.ivira.app.features.avasho.ui.onboarding.AVASHO_ONBOARDING_COMPLETED
import ai.ivira.app.features.config.data.ConfigRepository
import ai.ivira.app.features.config.ui.TileItem
import ai.ivira.app.features.home.data.CURRENT_CHANGELOG_VERSION_KEY
import ai.ivira.app.features.home.data.VersionRepository
import ai.ivira.app.features.home.ui.home.version.model.ChangelogView
import ai.ivira.app.features.home.ui.home.version.model.toChangelogView
import ai.ivira.app.features.home.ui.home.version.model.toVersionView
import ai.ivira.app.features.imazh.ui.onboarding.IMAZH_ONBOARDING_COMPLETED
import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.event.ViraPublisher
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
import ai.ivira.app.utils.ui.stateIn
import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CURRENT_TIME_PREF_KEY = "currentTimePrefKey"
private const val SHOWING_PERMISSION_REQUEST_INTERVAL = 7 * DateUtils.DAY_IN_MILLIS

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val versionRepository: VersionRepository,
    private val configRepository: ConfigRepository,
    private val sharedPref: SharedPreferences,
    private val uiException: UiException,
    aiEventPublisher: ViraPublisher,
    repository: DataStoreRepository,
    networkStatusTracker: NetworkStatusTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val isFirstRun = savedStateHandle.get<Boolean>("isFirstRun") ?: false

    private val _uiViewState = MutableStateFlow<UiStatus>(UiIdle)
    val uiViewState = _uiViewState.asStateFlow()

    private val aiEvent = aiEventPublisher.events.stateIn(initial = false)

    val networkStatus = networkStatusTracker.networkStatus.stateIn(initial = NetworkStatus.Unavailable)

    private val _shouldShowChangeLogBottomSheet = MutableStateFlow(
        getCurrentChangelogVersionFromSharedPref() < BuildConfig.VERSION_CODE
    )
    val shouldShowChangeLogBottomSheet = _shouldShowChangeLogBottomSheet.asStateFlow()

    val updatedChangelogList = versionRepository.getChangelog().map { changelogList ->
        val oldVersion = getCurrentChangelogVersionFromSharedPref()
        if (oldVersion < BuildConfig.VERSION_CODE) {
            updateChangelogVersion()
        }
        if (isFirstRun) {
            emptyList<ChangelogView>()
        } else {
            if (oldVersion == 0) {
                buildList<ChangelogView> {
                    changelogList.firstOrNull()
                        ?.takeIf { it.releaseNotes.isNotEmpty() }?.let { changeLog ->
                            add(changeLog.toChangelogView())
                        }
                }
            } else {
                changelogList.map { changeLog -> changeLog.toChangelogView() }
            }
        }
    }.flowOn(IO).stateIn(initial = emptyList())

    private var prefListener: SharedPreferences.OnSharedPreferenceChangeListener
    val changeLogList = versionRepository.getChangeLogFromLocal()
        .map { changeLogList ->
            changeLogList.map { changeLogDto ->
                changeLogDto.toVersionView()
            }
        }.stateIn(initial = emptyList())

    var shouldNavigate = mutableStateOf(false)
    var shouldNavigateToAvasho = mutableStateOf(false)
    var shouldNavigateToImazh = mutableStateOf(false)
    var shouldShowNotificationBottomSheet = false
        private set

    private var _showUpdateBottomSheet = MutableStateFlow(false)

    val shouldShowForceUpdateBottomSheet = combine(
        aiEvent,
        changeLogList
    ) { aiEvent, versionList ->
        aiEvent == ViraEvent.TokenExpired || versionList.any { version -> version.isForce }
    }.stateIn(initial = false)

    val showUpdateBottomSheet = combine(
        _showUpdateBottomSheet,
        changeLogList,
        shouldShowForceUpdateBottomSheet
    ) { showUpdateBottomSheet, changeLogList, shouldShowForceUpdateBottomSheet ->

        !shouldShowForceUpdateBottomSheet && showUpdateBottomSheet && changeLogList.isNotEmpty()
    }.stateIn(initial = false)

    var canShowBottomSheet = true
        private set

    var onboardingHasBeenShown = mutableStateOf(false)
        private set

    var avashoOnboardingHasBeenShown = mutableStateOf(false)
        private set

    var imazhOnboardingHasBeenShown = mutableStateOf(false)
        private set

    val unavailableTileToShowBottomSheet: MutableState<TileItem?> = mutableStateOf(null)

    init {

        if (isFirstRun) {
            updateChangelogVersion()
        }

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

        imazhOnboardingHasBeenShown.value = sharedPref.getBoolean(
            IMAZH_ONBOARDING_COMPLETED,
            false
        )

        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == AVASHO_ONBOARDING_COMPLETED) {
                avashoOnboardingHasBeenShown.value = sharedPref.getBoolean(
                    AVASHO_ONBOARDING_COMPLETED,
                    false
                )
            }
            if (key == IMAZH_ONBOARDING_COMPLETED) {
                imazhOnboardingHasBeenShown.value = sharedPref.getBoolean(
                    IMAZH_ONBOARDING_COMPLETED,
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

    fun navigateToImazh() {
        shouldNavigateToImazh.value = true
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
            _uiViewState.update { UiLoading }

            when (val result = configRepository.fetchVersions()) {
                is Success -> {
                    _uiViewState.update { UiSuccess }
                }

                is Error -> {
                    _uiViewState.update { UiError(uiException.getErrorMessage(result.error)) }
                }
            }
        }
    }

    private fun updateChangelogVersion() {
        viewModelScope.launch {
            sharedPref
                .edit()
                .putInt(CURRENT_CHANGELOG_VERSION_KEY, BuildConfig.VERSION_CODE)
                .apply()
        }
    }

    fun changeLogBottomSheetIsShow() {
        _shouldShowChangeLogBottomSheet.value = false
    }

    private fun getCurrentChangelogVersionFromSharedPref() =
        sharedPref.getInt(CURRENT_CHANGELOG_VERSION_KEY, 0)

    fun clearUiState() {
        _uiViewState.value = UiIdle
    }

    override fun onCleared() {
        super.onCleared()
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}