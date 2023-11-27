package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheet
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NotificationPermission
import ai.ivira.app.utils.common.CommonConstants.LANDING_URL
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.hasNotificationPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersion33orHigher
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AboutUs
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.shareText
import ai.ivira.app.utils.ui.sheets.AccessNotificationBottomSheet
import ai.ivira.app.utils.ui.theme.Blue_Grey_900_2
import ai.ivira.app.utils.ui.theme.Blue_gray_900
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Light_blue_50
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest.permission
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

    val (sheetSelected, setSelectedSheet) = rememberSaveable {
        mutableStateOf(HomeItemBottomSheetType.AvaSho)
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
                navController.navigate(ScreenRoutes.AvaNegarOnboarding.route)
            } else {
                navController.navigate(ScreenRoutes.AvaNegarArchiveList.route)
            }

            homeViewModel.shouldNavigate.value = false
        }
    }

    LaunchedEffect(Unit) {
        if (
            isSdkVersion33orHigher() &&
            !context.hasNotificationPermission() &&
            homeViewModel.shouldShowNotificationBottomSheet
        ) {
            setSelectedSheet(HomeItemBottomSheetType.NotificationPermission)
            coroutineScope.launch {
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
                            append(context.getString(string.lbl_introduce_text))
                            append("\n")
                            append(context.getString(string.lbl_download))
                            append("\n")
                            append(LANDING_URL)
                        }
                    )
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        drawerBackgroundColor = Blue_gray_900,
        drawerScrimColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerShape = RoundedCornerShape(0.dp),
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) { innerPadding ->
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(
                topEnd = 16.dp,
                topStart = 16.dp
            ),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetContent = sheetContent@{
                when (sheetSelected) {
                    HomeItemBottomSheetType.AvaSho -> {
                        HomeItemBottomSheet(
                            iconRes = drawable.img_ava_sho,
                            title = stringResource(id = string.lbl_ava_sho),
                            textBody = stringResource(
                                id = string.avasho_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.NeviseNama -> {
                        HomeItemBottomSheet(
                            iconRes = drawable.img_nevise_nama,
                            title = stringResource(id = string.lbl_nevise_nama),
                            textBody = stringResource(
                                id = string.nevise_nama_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.NeviseNegar -> {
                        HomeItemBottomSheet(
                            iconRes = drawable.img_nevise_negar,
                            title = stringResource(id = string.lbl_nevise_negar),
                            textBody = stringResource(
                                id = string.nevise_negar_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.ViraSiar -> {
                        HomeItemBottomSheet(
                            iconRes = drawable.img_virasiar,
                            title = stringResource(id = string.lbl_virasiar),
                            textBody = stringResource(
                                id = string.vira_sayar_item_bottomsheet_explain
                            ),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.NotificationPermission -> {
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
                    // navigate to ava negar
                    homeViewModel.navigate()
                },
                onItemClick = { homeItem ->
                    eventHandler.selectItem(HomeAnalytics.selectComingSoonItem(homeItem))
                    when (homeItem) {
                        HomeItemBottomSheetType.AvaSho -> {
                            setSelectedSheet(HomeItemBottomSheetType.AvaSho)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        HomeItemBottomSheetType.NeviseNegar -> {
                            setSelectedSheet(HomeItemBottomSheetType.NeviseNegar)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        HomeItemBottomSheetType.ViraSiar -> {
                            setSelectedSheet(HomeItemBottomSheetType.ViraSiar)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else {
                                    modalBottomSheetState.hide()
                                }
                            }
                        }

                        HomeItemBottomSheetType.NeviseNama -> {
                            setSelectedSheet(HomeItemBottomSheetType.NeviseNama)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 20.dp, bottom = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        ViraImage(
            drawable = drawable.ic_app_logo_name_linear,
            contentDescription = null
        )

        IconButton(
            onClick = { safeClick(openDrawer) },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ViraIcon(
                drawable = drawable.ic_menu,
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
    onItemClick: (HomeItemBottomSheetType) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeItem = remember { HomeItemScreen.items }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color_BG),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            elevation = 0.dp,
            shape = RoundedCornerShape(16.dp),
            onClick = {
                safeClick {
                    onAvanegarClick()
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(
                        end = 24.dp
                    )
                    .heightIn(min = 128.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViraImage(
                    drawable = drawable.img_ava_negar_2,
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
                        text = stringResource(id = string.lbl_ava_negar),
                        style = MaterialTheme.typography.h6,
                        color = Color_Text_1
                    )
                    Text(
                        text = stringResource(id = string.lbl_ava_negar_desc),
                        color = Light_blue_50,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                        .background(Color_Primary_200),
                    contentAlignment = Alignment.Center
                ) {
                    ViraImage(
                        drawable = drawable.ic_arrow_crooked,
                        contentDescription = "ic_arrow"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color_OutLine
            )
            Text(
                text = stringResource(id = string.coming_soon_vira),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.subtitle2,
                color = Color_Text_2,
                textAlign = TextAlign.Center
            )
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp),
                color = Color_OutLine
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 4.dp)
            .heightIn(min = 148.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            onClick = {
                safeClick {
                    onItemClick(item.homeItemType)
                }
            },
            elevation = 0.dp,
            backgroundColor = Color_Card
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            ) {
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

                Spacer(modifier = Modifier.size(16.dp))

                Surface(
                    shape = CircleShape,
                    border = BorderStroke(
                        0.5.dp,
                        color = Color_Card_Stroke
                    )
                ) {
                    Text(
                        text = stringResource(id = string.lbl_coming_soon),
                        modifier = Modifier
                            .background(Blue_Grey_900_2)
                            .padding(
                                horizontal = 17.dp,
                                vertical = 10.dp
                            ),
                        style = MaterialTheme.typography.overline,
                        color = Color_Text_3
                    )
                }
            }
        }

        ViraImage(
            drawable = item.icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
    }
}

@ViraDarkPreview
@Composable
private fun HomeBodyPreview() {
    ViraPreview {
        HomeBody(
            paddingValues = PaddingValues(0.dp),
            onAvanegarClick = {},
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