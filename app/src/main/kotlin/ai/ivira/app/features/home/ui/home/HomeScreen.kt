package ai.ivira.app.features.home.ui.home

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetDefaults
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.AvanegarScreenRoutes.AvaNegarArchiveList
import ai.ivira.app.features.ava_negar.AvanegarScreenRoutes.AvaNegarOnboarding
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.avasho.ui.AvashoScreenRoutes.AvaShoArchiveScreen
import ai.ivira.app.features.avasho.ui.AvashoScreenRoutes.AvaShoOnboardingScreen
import ai.ivira.app.features.config.ui.ConfigViewModel
import ai.ivira.app.features.hamahang.ui.HamahangAnalytics
import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.features.home.ui.HomeScreenRoutes
import ai.ivira.app.features.home.ui.HomeScreenRoutes.AboutUs
import ai.ivira.app.features.home.ui.HomeScreenRoutes.TermsOfServiceScreen
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheet
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.Changelog
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.ForceUpdate
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.LogoutConfirmation
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NotificationPermission
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.UnavailableTile
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.UpdateApp
import ai.ivira.app.features.home.ui.home.sheets.LogoutBottomSheetViewModel
import ai.ivira.app.features.home.ui.home.sheets.LogoutConfirmationBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.ChangelogBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.ForceUpdateScreen
import ai.ivira.app.features.home.ui.home.version.sheets.UpToDateBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.UpdateBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.UpdateLoadingBottomSheet
import ai.ivira.app.features.imazh.ui.ImazhScreenRoutes
import ai.ivira.app.features.login.ui.LoginAnalytics.logout
import ai.ivira.app.features.login.ui.LoginScreenRoutes
import ai.ivira.app.utils.common.CommonConstants.LANDING_URL
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.hasNotificationPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersion33orHigher
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.shareText
import ai.ivira.app.utils.ui.sheets.AccessNotificationBottomSheet
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Blue_Grey_900_2
import ai.ivira.app.utils.ui.theme.Blue_gray_900
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Deep_Purple_500
import ai.ivira.app.utils.ui.theme.Pink_100
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.HorizontalInfinitePager
import ai.ivira.app.utils.ui.widgets.TextAutoSize
import ai.ivira.app.utils.ui.widgets.TextAutoSizeRange
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

private const val MARQUEE_DELAY = 4000L
private const val CHANGE_COLOR_DELAY = 2000
private const val TEXT_ANIMATION_TRANSITION = 1000

@Composable
fun HomeScreenRoute(navController: NavHostController) {
    val activity = LocalContext.current as ComponentActivity
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(HomeAnalytics.screenViewHome)
    }

    HomeScreen(
        navController = navController,
        homeViewModel = hiltViewModel(),
        configViewModel = hiltViewModel(viewModelStoreOwner = activity),
        logoutViewModel = hiltViewModel()
    )
}

@Composable
private fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    configViewModel: ConfigViewModel,
    logoutViewModel: LogoutBottomSheetViewModel
) {
    val eventHandler = LocalEventHandler.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val context = LocalContext.current

    val changeLogList by homeViewModel.changeLogList.collectAsStateWithLifecycle()
    val uiState by homeViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)
    val networkStatus by homeViewModel.networkStatus.collectAsStateWithLifecycle()

    val hasVpnConnection by remember(networkStatus) {
        mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
    }

    val showUpdateBottomSheet by homeViewModel.showUpdateBottomSheet.collectAsStateWithLifecycle()

    var sheetSelected by rememberSaveable { mutableStateOf(UnavailableTile) }
    val sheetState = rememberViraBottomSheetState(confirmValueChange = { sheetSelected != ForceUpdate })

    val avanegarTile by configViewModel.avanegarTileConfig.collectAsStateWithLifecycle(initialValue = null)
    val avashoTile by configViewModel.avashoTileConfig.collectAsStateWithLifecycle(initialValue = null)
    val imazhTile by configViewModel.imazhTileConfig.collectAsStateWithLifecycle(initialValue = null)
    val hamahangTile by configViewModel.hamahangTileConfig.collectAsStateWithLifecycle(initialValue = null)

    val shouldShowForceUpdateBottomSheet by homeViewModel.shouldShowForceUpdateBottomSheet.collectAsStateWithLifecycle()
    val shouldShowChangeLogBottomSheet by homeViewModel.shouldShowChangeLogBottomSheet.collectAsStateWithLifecycle()
    val updatedChangelogList by homeViewModel.updatedChangelogList.collectAsStateWithLifecycle()
    val userToken by logoutViewModel.isUserLogin.collectAsStateWithLifecycle()

    val onItemClick: (selectedItemType: HomeItemType) -> Unit = remember {
        { itemType ->
            when (itemType) {
                HomeItemType.Avanegar -> {
                    if (avanegarTile?.available == false) {
                        homeViewModel.unavailableTileToShowBottomSheet.value = avanegarTile
                        sheetSelected = UnavailableTile
                        sheetState.show()
                    } else {
                        eventHandler.specialEvent(HomeAnalytics.openAvanegar)
                        homeViewModel.navigate()
                    }
                }
                HomeItemType.Avasho -> {
                    if (avashoTile?.available == false) {
                        homeViewModel.unavailableTileToShowBottomSheet.value = avashoTile
                        sheetSelected = UnavailableTile
                        sheetState.show()
                    } else {
                        eventHandler.specialEvent(HomeAnalytics.openAvasho)
                        homeViewModel.navigateToAvasho()
                    }
                }
                HomeItemType.Imazh -> {
                    if (imazhTile?.available == false) {
                        homeViewModel.unavailableTileToShowBottomSheet.value = imazhTile
                        sheetSelected = UnavailableTile
                        sheetState.show()
                    } else {
                        eventHandler.specialEvent(HomeAnalytics.openImazh)
                        homeViewModel.navigateToImazh()
                    }
                }
                HomeItemType.Hamahang -> {
                    if (hamahangTile?.available == false) {
                        homeViewModel.unavailableTileToShowBottomSheet.value = hamahangTile
                        sheetSelected = UnavailableTile
                        sheetState.show()
                    } else {
                        eventHandler.specialEvent(HomeAnalytics.openHamahang)
                        homeViewModel.navigateToHamahang()
                    }
                }
            }
        }
    }

    LaunchedEffect(userToken) {
        val value = userToken
        if (value is LogoutBottomSheetViewModel.TokenStatus.Value && value.token.isNullOrBlank()) {
            navController.navigate(
                route = LoginScreenRoutes.LoginMobileScreen.createRoute(
                    fromSplash = false
                )
            ) {
                popUpTo(route = HomeScreenRoutes.Home.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(
        configViewModel.shouldShowAvanegarUnavailableBottomSheet.value
    ) {
        if (configViewModel.shouldShowAvanegarUnavailableBottomSheet.value) {
            coroutineScope.launch {
                homeViewModel.unavailableTileToShowBottomSheet.value = avanegarTile
                sheetSelected = UnavailableTile
                sheetState.show()
            }
            configViewModel.resetAvanegarUnavailableFeature()
        }
    }

    LaunchedEffect(
        configViewModel.shouldShowAvashoUnavailableBottomSheet.value
    ) {
        if (configViewModel.shouldShowAvashoUnavailableBottomSheet.value) {
            coroutineScope.launch {
                homeViewModel.unavailableTileToShowBottomSheet.value = avashoTile
                sheetSelected = UnavailableTile
                sheetState.show()
            }
            configViewModel.resetAvashoUnavailableFeature()
        }
    }

    LaunchedEffect(
        configViewModel.shouldShowImazhUnavailableBottomSheet.value
    ) {
        if (configViewModel.shouldShowImazhUnavailableBottomSheet.value) {
            coroutineScope.launch {
                homeViewModel.unavailableTileToShowBottomSheet.value = imazhTile
                sheetSelected = UnavailableTile
                sheetState.show()
            }
            configViewModel.resetImazhUnavailableFeature()
        }
    }

    LaunchedEffect(
        configViewModel.shouldShowHamahangUnavailableBottomSheet.value
    ) {
        if (configViewModel.shouldShowHamahangUnavailableBottomSheet.value) {
            coroutineScope.launch {
                homeViewModel.unavailableTileToShowBottomSheet.value = hamahangTile
                sheetSelected = UnavailableTile
                sheetState.show()
            }
            configViewModel.resetHamahangUnavailableFeature()
        }
    }

    BackHandler(scaffoldState.drawerState.isOpen) {
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }

    LaunchedEffect(
        homeViewModel.onboardingHasBeenShown.value,
        homeViewModel.shouldNavigate.value
    ) {
        if (homeViewModel.shouldNavigate.value) {
            if (!homeViewModel.onboardingHasBeenShown.value) {
                // TODO: should this be here?
                eventHandler.onboardingEvent(AvanegarAnalytics.onboardingStart)
                navController.navigate(AvaNegarOnboarding.route)
            } else {
                navController.navigate(AvaNegarArchiveList.route)
            }

            homeViewModel.shouldNavigate.value = false
        }
    }

    LaunchedEffect(
        homeViewModel.avashoOnboardingHasBeenShown.value,
        homeViewModel.shouldNavigateToAvasho.value
    ) {
        if (homeViewModel.shouldNavigateToAvasho.value) {
            if (!homeViewModel.avashoOnboardingHasBeenShown.value) {
                navController.navigate(AvaShoOnboardingScreen.route)
            } else {
                navController.navigate(AvaShoArchiveScreen.route)
            }

            homeViewModel.shouldNavigateToAvasho.value = false
        }
    }

    LaunchedEffect(
        homeViewModel.imazhOnboardingHasBeenShown.value,
        homeViewModel.shouldNavigateToImazh.value
    ) {
        if (homeViewModel.shouldNavigateToImazh.value) {
            if (!homeViewModel.imazhOnboardingHasBeenShown.value) {
                navController.navigate(ImazhScreenRoutes.ImazhOnboardingScreen.route)
            } else {
                navController.navigate(ImazhScreenRoutes.ImazhArchiveListScreen.route)
            }
            homeViewModel.shouldNavigateToImazh.value = false
        }
    }

    LaunchedEffect(
        homeViewModel.hamahangOnboardingHasBeenShown.value,
        homeViewModel.shouldNavigateToHamahang.value
    ) {
        if (homeViewModel.shouldNavigateToHamahang.value) {
            if (!homeViewModel.hamahangOnboardingHasBeenShown.value) {
                eventHandler.onboardingEvent(HamahangAnalytics.onboardingStart)
                navController.navigate(HamahangScreenRoutes.HamahangOnboardingScreen.route)
            } else {
                navController.navigate(HamahangScreenRoutes.HamahangArchiveListScreen.route)
            }
            homeViewModel.shouldNavigateToHamahang.value = false
        }
    }

    LaunchedEffect(Unit) {
        if (
            isSdkVersion33orHigher() &&
            !context.hasNotificationPermission() &&
            homeViewModel.shouldShowNotificationBottomSheet
        ) {
            coroutineScope.launch {
                sheetSelected = NotificationPermission
                sheetState.show()
            }
        }
    }

    LaunchedEffect(showUpdateBottomSheet) {
        if (homeViewModel.canShowBottomSheet && showUpdateBottomSheet) {
            sheetSelected = UpdateApp
            sheetState.show()
        }
    }

    LaunchedEffect(shouldShowChangeLogBottomSheet, updatedChangelogList) {
        if (shouldShowChangeLogBottomSheet) {
            if (updatedChangelogList.isNotEmpty()) {
                sheetSelected = Changelog
                sheetState.show()
                homeViewModel.changeLogBottomSheetIsShow()
            }
        }
    }

    LaunchedEffect(shouldShowForceUpdateBottomSheet) {
        if (shouldShowForceUpdateBottomSheet) {
            sheetSelected = ForceUpdate
            sheetState.show()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeAppBar {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerContent = {
            DrawerHeader(
                userMobile = homeViewModel.userMobile.value,
                aboutUsOnClick = {
                    // first close drawer because if we click on about us and after that quickly click somewhere else,
                    // it's still open when we get back to main screen
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                        navController.navigate(AboutUs.route)
                    }
                },
                inviteFriendOnclick = {
                    eventHandler.specialEvent(HomeAnalytics.introduceToFriends)
                    shareText(
                        context,
                        buildString {
                            append(context.getString(R.string.lbl_introduce_text))
                            append("\n")
                            append(context.getString(R.string.lbl_download))
                            append("\n")
                            append(LANDING_URL)
                        }
                    )
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                onUpdateClick = {
                    eventHandler.specialEvent(HomeAnalytics.checkUpdate)
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }

                    if (networkStatus is NetworkStatus.Unavailable) {
                        showMessage(
                            snackbarHostState,
                            coroutineScope,
                            context.getString(R.string.msg_internet_disconnected)
                        )
                        return@DrawerHeader
                    }

                    if (hasVpnConnection) {
                        showMessage(
                            snackbarHostState,
                            coroutineScope,
                            context.getString(R.string.msg_vpn_is_connected_error)
                        )
                        return@DrawerHeader
                    }

                    sheetSelected = UpdateApp
                    sheetState.show()

                    homeViewModel.getUpdateList()
                },
                onTermsOfServiceClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                    navController.navigate(TermsOfServiceScreen.route)
                },
                onLogoutClick = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }

                    sheetSelected = LogoutConfirmation
                    sheetState.show()
                }
            )
        },
        drawerBackgroundColor = Blue_gray_900,
        drawerScrimColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerShape = RoundedCornerShape(0.dp),
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        snackbarHost = { snackBarHost ->
            SnackBar(
                snackbarHostState = snackBarHost,
                paddingBottom = 32.dp,
                maxLine = 2
            )
        }
    ) { innerPadding ->

        val list = remember { HomeItemScreen.mainItemList }
        val bannerList = remember { HomeItemScreen.bannerItemList }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(0.6f)
            ) {
                items(
                    items = list,
                    key = { item ->
                        item.title
                    }
                ) {
                    HomeItem(
                        item = it,
                        onItemClick = { item ->
                            onItemClick(item)
                        }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.size(24.dp))
                        HorizontalInfinitePager(
                            realItemSize = bannerList.size,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2.34f),
                            itemContent = { index ->
                                val item = bannerList[index]

                                ViraImage(
                                    drawable = item.banner,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            safeClick {
                                                onItemClick(item.homeItemType)
                                            }
                                        }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (sheetState.showBottomSheet) {
        val selectedSheetUpdated by rememberUpdatedState(newValue = sheetSelected)

        ViraBottomSheet(
            sheetState = sheetState,
            isDismissibleOnTouchOutside = selectedSheetUpdated != LogoutConfirmation,
            isDismissibleOnDrag = selectedSheetUpdated != LogoutConfirmation,
            properties = ViraBottomSheetDefaults.properties(shouldDismissOnBackPress = false),
            onBackPressed = {
                when (selectedSheetUpdated) {
                    UpdateApp,
                    NotificationPermission,
                    UnavailableTile,
                    Changelog -> sheetState.hide()
                    ForceUpdate -> (context as Activity).finish()
                    LogoutConfirmation -> {
                        sheetState.hide()
                        logoutViewModel.resetLogoutRequest()
                    }
                }
            }
        ) {
            ViraBottomSheetContent(targetState = sheetSelected) {
                when (sheetSelected) {
                    UpdateApp -> {
                        if (showUpdateBottomSheet || uiState is UiSuccess) {
                            if (changeLogList.isNotEmpty()) {
                                UpdateBottomSheet(
                                    item = changeLogList,
                                    onUpdateClick = {
                                        eventHandler.specialEvent(HomeAnalytics.updateApp)
                                        sheetState.hide()
                                        kotlin.runCatching {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data = Uri.parse(BuildConfig.SHARE_URL)
                                            context.startActivity(intent)
                                        }
                                    },
                                    onLaterClick = {
                                        eventHandler.specialEvent(HomeAnalytics.showUpdateLater)
                                        homeViewModel.showLater()
                                        sheetState.hide()
                                    }
                                )
                            } else {
                                UpToDateBottomSheet(onUnderstoodClick = { sheetState.hide() })
                            }
                        } else if (uiState is UiLoading) {
                            UpdateLoadingBottomSheet()
                        } else if (uiState is UiError) {
                            sheetState.hide()
                            showMessage(
                                snackbarHostState,
                                coroutineScope,
                                context.getString(R.string.msg_updating_failed_please_try_again_later)
                            )
                        }

                        homeViewModel.doNotShowUpdateBottomSheetUntilNextLaunch()
                    }
                    ForceUpdate -> {
                        ForceUpdateScreen(
                            onUpdateClick = {
                                ContextCompat.startActivity(
                                    context,
                                    Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.SHARE_URL)),
                                    null
                                )
                            }
                        )
                    }
                    NotificationPermission -> {
                        if (!isSdkVersion33orHigher()) return@ViraBottomSheetContent
                        val notificationPermissionLauncher = rememberLauncherForActivityResult(
                            RequestPermission()
                        ) { isGranted ->
                            if (!isGranted) {
                                homeViewModel.putDeniedPermissionToSharedPref(
                                    permission = permission.POST_NOTIFICATIONS,
                                    deniedPermanently = isPermissionDeniedPermanently(
                                        activity = context as Activity,
                                        permission = permission.POST_NOTIFICATIONS
                                    )
                                )
                            }
                        }

                        AccessNotificationBottomSheet(
                            isPermissionDeniedPermanently = {
                                homeViewModel.hasDeniedPermissionPermanently(
                                    permission.POST_NOTIFICATIONS
                                )
                            },
                            onCancelClick = {
                                homeViewModel.putCurrentTimeDayToSharedPref()
                                sheetState.hide()
                            },
                            onEnableClick = {
                                notificationPermissionLauncher.launch(
                                    permission.POST_NOTIFICATIONS
                                )

                                sheetState.hide()
                            },
                            onSettingClick = {
                                navigateToAppSettings(activity = context as Activity)
                                sheetState.hide()
                            }
                        )
                        homeViewModel.doNotShowUtilNextLaunch()
                    }
                    UnavailableTile -> {
                        homeViewModel.unavailableTileToShowBottomSheet.value?.let { unavailableTile ->
                            HomeItemBottomSheet(
                                iconRes = unavailableTile.iconRes,
                                title = stringResource(id = unavailableTile.titleRes),
                                textBody = unavailableTile.unavailableStateMessage,
                                action = { sheetState.hide() }
                            )
                        }
                    }
                    Changelog -> {
                        ChangelogBottomSheet(
                            item = updatedChangelogList,
                            onUnderstoodClick = { sheetState.hide() }
                        )
                    }
                    LogoutConfirmation -> {
                        LogoutConfirmationBottomSheet(
                            viewModel = logoutViewModel,
                            cancelAction = { sheetState.hide() },
                            onErrorCallback = {
                                sheetState.hide()
                                showMessage(snackbarHostState, coroutineScope, it.message)
                            },
                            onSuccessCallback = {
                                eventHandler.specialEvent(logout)
                                sheetState.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeAppBar(openDrawer: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 8.dp)
    ) {
        ViraImage(
            drawable = R.drawable.ic_app_logo_name_linear,
            contentDescription = null
        )

        IconButton(
            onClick = { safeClick(openDrawer) },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ViraIcon(
                drawable = R.drawable.ic_menu,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color_Text_2
            )
        }
    }
}

@Composable
private fun HomeItem(
    item: HomeItemScreen,
    onItemClick: (HomeItemType) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconSize = 67.dp
    val halfOfIconSize = remember(iconSize) { iconSize / 2 }

    Box(modifier = modifier.height(148.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = halfOfIconSize)
                .background(Color_Card, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    safeClick { onItemClick(item.homeItemType) }
                }
        ) {
            Spacer(modifier = Modifier.size(halfOfIconSize))

            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1
            )

            Spacer(modifier = Modifier.size(4.dp))

            if (item.isComingSoon) {
                MarqueeText(
                    text = item.description,
                    modifier = Modifier.fillMaxWidth(0.45f)
                )
            } else {
                Text(
                    text = stringResource(id = item.description),
                    style = MaterialTheme.typography.labelMedium,
                    color = item.textColor
                )
            }
        }

        ViraImage(
            drawable = item.icon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun MarqueeText(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    var index by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (isActive) {
            yield()
            delay(MARQUEE_DELAY)
            index = (index + 1) % 2
        }
    }

    val textColor by animateColorAsState(
        targetValue = if (index == 0) Color_Text_2 else Pink_100,
        animationSpec = tween(CHANGE_COLOR_DELAY),
        label = "color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Blue_Grey_900_2, RoundedCornerShape(18.dp))
            .border(0.5.dp, Deep_Purple_500, RoundedCornerShape(18.dp))
    ) {
        AnimatedContent(
            targetState = index,
            label = "text",
            transitionSpec = {
                ContentTransform(
                    slideIn(
                        initialOffset = { IntOffset(-300, 0) },
                        animationSpec = tween(TEXT_ANIMATION_TRANSITION)
                    ),
                    slideOut(
                        targetOffset = { IntOffset(300, 0) },
                        animationSpec = tween(TEXT_ANIMATION_TRANSITION)
                    )
                )
            }
        ) { itemIndex ->
            val itemRes = if (itemIndex == 0) R.string.coming_soon else text
            TextAutoSize(
                text = stringResource(id = itemRes),
                textScale = TextAutoSizeRange(
                    min = 8.sp,
                    max = MaterialTheme.typography.overline.fontSize
                ),
                style = MaterialTheme.typography.overline,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun HomeItemPreview() {
    ViraPreview {
        HomeItem(
            item = HomeItemScreen.mainItemList.first(),
            onItemClick = {}
        )
    }
}

@ViraDarkPreview
@Composable
private fun HomeAppBarPreview() {
    ViraPreview {
        HomeAppBar(openDrawer = {})
    }
}