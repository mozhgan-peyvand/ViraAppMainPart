package ai.ivira.app.features.home.ui.home

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheet
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.Imazh
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NeviseNegar
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NotificationPermission
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.UpdateApp
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.ViraSiar
import ai.ivira.app.features.home.ui.home.version.sheets.UpToDateBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.UpdateBottomSheet
import ai.ivira.app.features.home.ui.home.version.sheets.UpdateLoadingBottomSheet
import ai.ivira.app.utils.common.CommonConstants.LANDING_URL
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.hasNotificationPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersion33orHigher
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AboutUs
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoArchiveScreen
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoOnboardingScreen
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.shareText
import ai.ivira.app.utils.ui.sheets.AccessNotificationBottomSheet
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Blue_gray_900
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Home_Avasho_Subtitle
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Light_blue_50
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun HomeScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(HomeAnalytics.screenViewHome)
    }

    HomeScreen(
        navController = navController,
        homeViewModel = hiltViewModel()
    )
}

@Composable
private fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    val eventHandler = LocalEventHandler.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val context = LocalContext.current

    val changeLogList by homeViewModel.changeLogList.collectAsStateWithLifecycle()
    val uiState by homeViewModel.uiViewState.collectAsStateWithLifecycle()
    val networkStatus by homeViewModel.networkStatus.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val hasVpnConnection by remember(networkStatus) {
        mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
    }

    val showUpdateBottomSheet by homeViewModel.showUpdateBottomSheet.collectAsStateWithLifecycle()

    val (sheetSelected, setSelectedSheet) = rememberSaveable {
        mutableStateOf(Imazh)
    }

    BackHandler(scaffoldState.drawerState.isOpen) {
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }

    BackHandler(modalBottomSheetState.isVisible) {
        coroutineScope.launch {
            modalBottomSheetState.hide()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiError) {
            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.msg_updating_failed_please_try_again_later)
            )

            // fixme should remove it, replace stateFlow with sharedFlow in viewModel
            homeViewModel.clearUiState()
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
                navController.navigate(ScreenRoutes.AvaNegarOnboarding.route)
            } else {
                navController.navigate(ScreenRoutes.AvaNegarArchiveList.route)
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

    LaunchedEffect(Unit) {
        if (
            isSdkVersion33orHigher() &&
            !context.hasNotificationPermission() &&
            homeViewModel.shouldShowNotificationBottomSheet
        ) {
            setSelectedSheet(NotificationPermission)
            coroutineScope.launch {
                if (!modalBottomSheetState.isVisible) {
                    modalBottomSheetState.show()
                }
            }
        }
    }

    // fixme hamburger icon of drawer is clickable when bottomSheet is open
    //  because ModalBottomSheet is child of Scaffold
    LaunchedEffect(scaffoldState.drawerState.isOpen) {
        if (modalBottomSheetState.isVisible) {
            coroutineScope.launch {
                modalBottomSheetState.hide()
            }
        }
    }

    LaunchedEffect(showUpdateBottomSheet) {
        if (homeViewModel.canShowBottomSheet && showUpdateBottomSheet) {
            setSelectedSheet(UpdateApp)

            coroutineScope.launch {
                modalBottomSheetState.hide()
                if (!modalBottomSheetState.isVisible) {
                    modalBottomSheetState.show()
                }
            }
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

                    setSelectedSheet(UpdateApp)
                    coroutineScope.launch {
                        if (!modalBottomSheetState.isVisible) {
                            modalBottomSheetState.show()
                        } else {
                            modalBottomSheetState.hide()
                        }
                    }

                    homeViewModel.getUpdateList()
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
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetContent = sheetContent@{
                when (sheetSelected) {
                    Imazh -> {
                        HomeItemBottomSheet(
                            iconRes = R.drawable.img_imazh_1,
                            title = stringResource(id = R.string.lbl_imazh),
                            textBody = stringResource(
                                id = R.string.imazh_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    NeviseNegar -> {
                        HomeItemBottomSheet(
                            iconRes = R.drawable.img_nevise_negar,
                            title = stringResource(id = R.string.lbl_nevise_negar),
                            textBody = stringResource(
                                id = R.string.nevise_negar_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ViraSiar -> {
                        HomeItemBottomSheet(
                            iconRes = R.drawable.img_virasiar,
                            title = stringResource(id = R.string.lbl_virasiar),
                            textBody = stringResource(
                                id = R.string.vira_sayar_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    UpdateApp -> {
                        if (showUpdateBottomSheet || uiState is UiSuccess) {
                            if (changeLogList.isNotEmpty()) {
                                UpdateBottomSheet(
                                    item = changeLogList,
                                    onUpdateClick = {
                                        eventHandler.specialEvent(HomeAnalytics.updateApp)
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                        kotlin.runCatching {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data = Uri.parse(BuildConfig.SHARE_URL)
                                            context.startActivity(intent)
                                        }
                                    },
                                    onLaterClick = {
                                        eventHandler.specialEvent(HomeAnalytics.showUpdateLater)
                                        homeViewModel.showLater()
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                )
                            } else {
                                UpToDateBottomSheet(
                                    onUnderstoodClick = {
                                        coroutineScope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                )
                            }
                        } else if (uiState is UiLoading) {
                            UpdateLoadingBottomSheet()
                        } else if (uiState is UiLoading) {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        }

                        homeViewModel.doNotShowUpdateBottomSheetUntilNextLaunch()
                    }

                    NotificationPermission -> {
                        if (!isSdkVersion33orHigher()) return@sheetContent
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
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            onEnableClick = {
                                notificationPermissionLauncher.launch(
                                    permission.POST_NOTIFICATIONS
                                )

                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            onSettingClick = {
                                navigateToAppSettings(activity = context as Activity)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                        homeViewModel.doNotShowUtilNextLaunch()
                    }
                }
            }
        ) {
            HomeBody(
                paddingValues = innerPadding,
                onAvanegarClick = {
                    eventHandler.specialEvent(HomeAnalytics.openAvanegar)
                    homeViewModel.navigate()
                },
                onAvashoClick = {
                    eventHandler.specialEvent(HomeAnalytics.openAvasho)
                    homeViewModel.navigateToAvasho()
                },
                onItemClick = { homeItem ->
                    when (homeItem) {
                        NeviseNegar -> {
                            eventHandler.specialEvent(HomeAnalytics.selectComingSoonItem(homeItem))
                            setSelectedSheet(NeviseNegar)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        ViraSiar -> {
                            eventHandler.specialEvent(HomeAnalytics.selectComingSoonItem(homeItem))
                            setSelectedSheet(ViraSiar)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        Imazh -> {
                            eventHandler.specialEvent(HomeAnalytics.selectComingSoonItem(homeItem))
                            setSelectedSheet(Imazh)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        NotificationPermission -> {}

                        UpdateApp -> {}
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun HomeAppBar(openDrawer: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 20.dp, bottom = 22.dp)
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
private fun HomeBody(
    paddingValues: PaddingValues,
    onAvanegarClick: () -> Unit,
    onAvashoClick: () -> Unit,
    onItemClick: (HomeItemBottomSheetType) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeItem = remember { HomeItemScreen.items }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color_BG)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 0.dp,
            onClick = {
                safeClick {
                    onAvanegarClick()
                }
            },
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(end = 24.dp)
                    .heightIn(min = 128.dp)
            ) {
                ViraImage(
                    drawable = R.drawable.img_ava_negar_2,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .size(width = 68.dp, height = 80.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.lbl_ava_negar),
                        style = MaterialTheme.typography.h6,
                        color = Color_Text_1
                    )
                    Text(
                        text = stringResource(id = R.string.lbl_ava_negar_desc),
                        color = Light_blue_50,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                        .background(Color_Primary_200)
                ) {
                    ViraImage(
                        drawable = R.drawable.ic_arrow_crooked,
                        colorFilter = ColorFilter.tint(Color.Black),
                        contentDescription = null
                    )
                }
            }
        }
        Card(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = 0.dp,
            onClick = {
                safeClick {
                    onAvashoClick()
                }
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(end = 24.dp)
                    .heightIn(min = 128.dp)
            ) {
                ViraImage(
                    drawable = R.drawable.img_ava_sho_2,
                    contentDescription = stringResource(id = R.string.lbl_ava_sho_desc),
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .size(width = 68.dp, height = 80.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.lbl_ava_sho),
                        style = MaterialTheme.typography.h6,
                        color = Color_Text_1
                    )
                    Text(
                        text = stringResource(id = R.string.lbl_ava_sho_desc),
                        color = Color_Home_Avasho_Subtitle,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                        .background(Color_Primary_200)
                ) {
                    ViraImage(
                        drawable = R.drawable.ic_arrow_crooked,
                        colorFilter = ColorFilter.tint(Color.Black),
                        contentDescription = null
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
        ) {
            Divider(
                color = Color_OutLine,
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )

            Text(
                text = stringResource(id = R.string.coming_soon_vira),
                style = MaterialTheme.typography.subtitle2,
                color = Color_Text_2,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Divider(
                color = Color_OutLine,
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(homeItem) { item ->
                HomeBodyItem(
                    item = item,
                    onItemClick = { onItemClick(item.homeItemType) }
                )
            }
        }
    }
}

@Composable
fun HomeBodyItem(
    item: HomeItemScreen,
    onItemClick: (HomeItemBottomSheetType) -> Unit
) {
    Card(
        elevation = 0.dp,
        backgroundColor = Color_Card,
        onClick = {
            safeClick {
                onItemClick(item.homeItemType)
            }
        },
        modifier = Modifier
            .background(Color.Transparent)
            .heightIn(min = 148.dp)
            .widthIn(min = 156.dp)
            .padding(top = 32.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        ) {
            ViraImage(
                drawable = item.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = stringResource(id = item.description),
                style = MaterialTheme.typography.labelMedium,
                color = item.textColor
            )
        }
    }
}

@ViraDarkPreview
@Composable
private fun HomeBodyPreview() {
    ViraPreview {
        HomeBody(
            paddingValues = PaddingValues(0.dp),
            onAvanegarClick = {},
            onItemClick = {},
            onAvashoClick = {}
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