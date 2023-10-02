package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.home.HomeItemScreen
import ir.part.app.intelligentassistant.utils.ui.Constants.CAFEBAZAAR_LINK
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.shareText
import ir.part.app.intelligentassistant.utils.ui.theme.Blue_Grey_900_2
import ir.part.app.intelligentassistant.utils.ui.theme.Blue_gray_900
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Bottom_Sheet
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.utils.ui.theme.Color_On_Surface_Variant
import ir.part.app.intelligentassistant.utils.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.ui.theme.Light_blue_50
import ir.part.app.intelligentassistant.utils.ui.theme.labelMedium
import kotlinx.coroutines.launch
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val modalBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
    val (sheetSelected, setSelectedSheet) = rememberSaveable {
        mutableStateOf(HomeItemBottomSheetType.AvaSho)
    }

    LaunchedEffect(
        homeViewModel.onboardingHasBeenShown.value,
        homeViewModel.shouldNavigate.value
    ) {

        if (homeViewModel.shouldNavigate.value) {
            if (!homeViewModel.onboardingHasBeenShown.value)
                navController.navigate(ScreenRoutes.AvaNegarOnboarding.route)
            else
                navController.navigate(ScreenRoutes.AvaNegarArchiveList.route)

            homeViewModel.shouldNavigate.value = false
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
                    navController.navigate(ScreenRoutes.AboutUs.route)
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                inviteFriendOnclick = {
                    shareText(
                        context,
                        buildString {
                            append(context.getString(R.string.lbl_introduce_text))
                            append("\n")
                            append(context.getString(R.string.lbl_download))
                            append("\n")
                            append(CAFEBAZAAR_LINK)
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
            sheetContent = {
                when (sheetSelected) {
                    HomeItemBottomSheetType.AvaSho -> {
                        HomeItemBottomSheet(iconRes = painterResource(id = R.drawable.img_ava_sho),
                            title = stringResource(id = R.string.lbl_ava_sho),
                            textBody = stringResource(id = R.string.avasho_item_bottomsheet_explain),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.NeviseNama -> {
                        HomeItemBottomSheet(iconRes = painterResource(id = R.drawable.img_nevise_nama),
                            title = stringResource(id = R.string.lbl_nevise_nama),
                            textBody = stringResource(id = R.string.nevise_nama_item_bottomsheet_explain),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )

                    }

                    HomeItemBottomSheetType.NeviseNegar -> {

                        HomeItemBottomSheet(iconRes = painterResource(id = R.drawable.img_nevise_negar),
                            title = stringResource(id = R.string.lbl_nevise_negar),
                            textBody = stringResource(id = R.string.nevise_negar_item_bottomsheet_explain),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    HomeItemBottomSheetType.ViraSiar -> {
                        HomeItemBottomSheet(iconRes = painterResource(id = R.drawable.img_virasiar),
                            title = stringResource(id = R.string.lbl_virasiar),
                            textBody = stringResource(id = R.string.vira_sayar_item_bottomsheet_explain),
                            action = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }
                }
            }) {
            HomeBody(
                paddingValues = innerPadding,
                onAvanegarClick = {
                    // navigate to ava negar
                    homeViewModel.navigate()
                },
                onItemClick = { homeItem ->
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
        Image(
            painter = painterResource(id = AIResource.drawable.ic_app_log_name_description),
            contentDescription = null,
            modifier = Modifier
        )

        IconButton(
            onClick = { safeClick(openDrawer) },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_menu),
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
    modifier: Modifier = Modifier,
    onItemClick: (HomeItemBottomSheetType) -> Unit
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
                        top = 18.dp,
                        bottom = 18.dp,
                        start = 24.dp,
                        end = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = AIResource.drawable.img_voice),
                    contentDescription = null,
                    modifier = Modifier.size(width = 68.dp, height = 80.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = AIResource.string.lbl_ava_negar),
                        style = MaterialTheme.typography.h6,
                        color = Color_Text_1
                    )
                    Text(
                        text = stringResource(id = AIResource.string.lbl_ava_negar_desc),
                        color = Light_blue_50,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                        .background(Color_On_Surface_Variant),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = AIResource.drawable.ic_arrow_crooked),
                        contentDescription = "ic_arrow"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
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
                text = stringResource(id = AIResource.string.coming_soon_vira),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.subtitle2,
                color = Color_Text_2, textAlign = TextAlign.Center
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
                    onItemClick = { onItemClick(item.homeItemType) })
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
            .aspectRatio(0.90f)
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 4.dp),
        contentAlignment = Alignment.TopCenter,
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
                        text = stringResource(id = AIResource.string.lbl_coming_soon),
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

        Image(
            painter = painterResource(id = item.icon),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
    }
}


@Preview
@Composable
private fun HomeBodyPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HomeBody(
                PaddingValues(0.dp), onAvanegarClick = {}, onItemClick =
                {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun HomeAppBarPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HomeAppBar {}
        }
    }
}