package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.DeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.Delete
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.DeleteConfirmation
import ai.ivira.app.features.imazh.ui.archive.model.ImazhArchiveView
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.KEY_NEW_IMAGE_RESULT
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.hide
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhNewImageDescriptorScreen
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Background_Menu
import ai.ivira.app.utils.ui.theme.Color_Blue_Grey_800_945
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.toBitmap
import ai.ivira.app.utils.ui.widgets.ViraBannerInfo
import ai.ivira.app.utils.ui.widgets.ViraBannerWithAnimation
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImazhArchiveListScreenRoute(navController: NavHostController) {
    ImazhArchiveListScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun ImazhArchiveListScreen(
    navController: NavHostController,
    viewModel: ImazhArchiveListViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val isGrid by viewModel.isGrid.collectAsState()
    val archiveFiles by viewModel.allArchiveFiles.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val uiViewState by viewModel.uiViewState.collectAsState(UiIdle)
    var isVisible by rememberSaveable { mutableStateOf(true) }
    val isScrollingUp by listState.isScrollingUp()
    val isScrolledDown by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        }
    }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { true }
    )
    var selectedSheet by rememberSaveable { mutableStateOf(Delete) }
    val selectedMenuItem = remember { mutableStateOf<ImazhProcessedFileView?>(null) }

    val newImageResult = getNewImageResult(navController)?.collectAsState(initial = false)
    LaunchedEffect(newImageResult, isScrolledDown) {
        if (newImageResult?.value == true && isScrolledDown) {
            listState.scrollToItem(0)
            resetNewImageResult(navController)
        }
    }

    LaunchedEffect(isGrid) { isVisible = true }
    LaunchedEffect(isScrollingUp) { isVisible = isScrollingUp }

    BackHandler(modalBottomSheetState.isVisible) {
        if (modalBottomSheetState.isVisible) {
            modalBottomSheetState.hide(coroutineScope)
        }
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            ImazhArchiveAppBar(
                onBackClick = navController::navigateUp,
                isGrid = isGrid,
                showListTypeIcon = archiveFiles.isNotEmpty(),
                onChangeListTypeClick = {
                    viewModel.saveListType(!isGrid)
                }
            )
        }
    ) { paddingValues ->
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = modalBottomSheetState,
            sheetContent = sheetContent@{
                when (selectedSheet) {
                    Delete -> {
                        DeleteBottomSheet(
                            fileName = "",
                            onDelete = {
                                selectedSheet = DeleteConfirmation
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    }
                                }
                            }
                        )
                    }

                    DeleteConfirmation -> {
                        val info = selectedMenuItem.value ?: return@sheetContent

                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                viewModel.removeImage(info.id, info.filePath)
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            cancelAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            fileName = ""
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (archiveFiles.isEmpty()) {
                    ArchiveEmptyBody()
                } else {
                    Column {
                        // TODO: Don't show it when download state is added
                        val isNetworkAvailable by remember(networkStatus) {
                            mutableStateOf(networkStatus is NetworkStatus.Available)
                        }
                        val hasVpnConnection by remember(networkStatus) {
                            mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
                        }
                        val isBannerError by remember(uiViewState) {
                            mutableStateOf(uiViewState.let { it is UiError && !it.isSnack })
                        }
                        ViraBannerWithAnimation(
                            isVisible = !isNetworkAvailable || hasVpnConnection || isBannerError, // FIXME: Should this be displaying only if upload is in progress?
                            bannerInfo = if (uiViewState is UiError) {
                                ViraBannerInfo.Error(
                                    message = (uiViewState as UiError).message,
                                    iconRes = R.drawable.ic_failure_network
                                )
                            } else if (hasVpnConnection) {
                                ViraBannerInfo.Warning(
                                    message = stringResource(id = R.string.msg_vpn_is_connected_error),
                                    iconRes = R.drawable.ic_warning_vpn
                                )
                            } else {
                                ViraBannerInfo.Error(
                                    message = stringResource(id = R.string.msg_internet_disconnected),
                                    iconRes = R.drawable.ic_failure_network
                                )
                            }
                        )

                        ArchiveListContent(
                            archiveFiles = archiveFiles,
                            listState = listState,
                            isGrid = isGrid,
                            isInDownloadQueue = { id -> viewModel.isInDownloadQueue(id) },
                            onProcessedItemClick = { id ->
                                navController.navigate(
                                    ScreenRoutes.ImazhDetailsScreen.createRoute(
                                        id
                                    )
                                )
                            },
                            onMenuClick = { itemProcessed ->
                                selectedMenuItem.value = itemProcessed
                                selectedSheet = Delete
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    }
                                }
                            }
                        )
                    }
                }

                ImazhArchiveFab(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onClick = onClick@{
                        navController.navigate(route = ImazhNewImageDescriptorScreen.route)
                    },
                    isVisible = isVisible
                )
            }
        }
    }
}

@Composable
private fun ArchiveListContent(
    archiveFiles: List<ImazhArchiveView>,
    listState: LazyGridState,
    isInDownloadQueue: (Int) -> Boolean,
    isGrid: Boolean,
    onProcessedItemClick: (Int) -> Unit,
    onMenuClick: (ImazhProcessedFileView) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = remember(isGrid) {
        if (isGrid) GridCells.Fixed(2) else GridCells.Fixed(1)
    }
    val horizontalArrangement = remember(isGrid) {
        if (isGrid) Arrangement.spacedBy(16.dp) else Arrangement.Center
    }

    LazyVerticalGrid(
        columns = columns,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = horizontalArrangement,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(
            count = archiveFiles.size,
            key = {
                when (val item = archiveFiles[it]) {
                    is ImazhProcessedFileView -> item.id
                    else -> Unit
                }
            }
        ) { index ->
            when (val item = archiveFiles[index]) {
                is ImazhProcessedFileView -> {
                    ImazhProcessedItem(
                        item = item,
                        isSmallItem = isGrid,
                        isInDownloadQueue = { id -> isInDownloadQueue(id) },
                        onClick = { id -> onProcessedItemClick(id) },
                        onMenuClick = { processedItem -> onMenuClick(processedItem) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImazhArchiveAppBar(
    isGrid: Boolean,
    showListTypeIcon: Boolean,
    onBackClick: () -> Unit,
    onChangeListTypeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = {
                safeClick {
                    onBackClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                modifier = Modifier.padding(12.dp),
                contentDescription = stringResource(id = R.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_imazh),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        if (showListTypeIcon) {
            IconButton(
                onClick = {
                    safeClick {
                        onChangeListTypeClick()
                    }
                }
            ) {
                ViraIcon(
                    drawable = if (isGrid) {
                        R.drawable.ic_list_column
                    } else {
                        R.drawable.ic_list_grid
                    },
                    contentDescription = stringResource(
                        if (isGrid) {
                            R.string.desc_column
                        } else {
                            R.string.desc_grid
                        }
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ArchiveEmptyBody(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ViraImage(
                drawable = R.drawable.img_main_page,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_dose_not_exist_any_file),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_make_your_first_file),
                style = MaterialTheme.typography.caption,
                color = Color_Text_3,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(53.dp))

        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(80.dp))

            ViraImage(
                drawable = R.drawable.ic_arrow,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )
        }

        Spacer(modifier = Modifier.size(60.dp))
    }
}

@Composable
private fun ImazhArchiveFab(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp
        ),
        visible = isVisible,
        enter = slideInVertically(
            // Enters by sliding down from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 250,
                easing = EaseInOut
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 150,
                easing = EaseInOut
            )
        )
    ) {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                safeClick {
                    onClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_add,
                contentDescription = null,
                tint = Color_White
            )
        }
    }
}

@Composable
private fun ImazhProcessedItem(
    item: ImazhProcessedFileView,
    isSmallItem: Boolean,
    isInDownloadQueue: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onMenuClick: (ImazhProcessedFileView) -> Unit,
    modifier: Modifier = Modifier
) {
    val textPaddingModifier by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            } else {
                Modifier.padding(vertical = 12.dp, horizontal = 22.dp)
            }
        )
    }
    val isInQueue by remember(item) { mutableStateOf(isInDownloadQueue(item.id)) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color_Card)
            .clickable(enabled = !isInQueue) {
                safeClick {
                    onClick(item.id)
                }
            }
    ) {
        if (!isInQueue) {
            val file = File(item.filePath)
            val imageBitmap: Bitmap? by remember(file) { mutableStateOf(file.toBitmap()) }

            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        Text(
            text = item.prompt,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = if (isSmallItem) {
                MaterialTheme.typography.body2
            } else {
                MaterialTheme.typography.body1
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color_Blue_Grey_800_945.copy(alpha = 0.75f))
                .then(textPaddingModifier)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        )

        if (isInQueue) {
            IconButton(
                onClick = {
                    safeClick { onMenuClick(item) }
                },
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp)
                    .size(42.dp)
                    .align(Alignment.TopEnd)
                    .background(Color_Background_Menu, RoundedCornerShape(12.dp))
            ) {
                ViraImage(
                    drawable = R.drawable.ic_dots_menu,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.lbl_downloading_image),
                color = Color_Text_2,
                style = if (isSmallItem) {
                    MaterialTheme.typography.body2
                } else {
                    MaterialTheme.typography.body1
                },
                modifier = Modifier.align(Alignment.Center)
            )

            ImageLoadingProgress(
                isSmallItem = isSmallItem,
                progress = item.downloadingPercent
            )
        }
    }
}

@Composable
private fun ImageLoadingProgress(
    isSmallItem: Boolean,
    modifier: Modifier = Modifier,
    progress: Float = -1f
) {
    val paddingTop by remember(isSmallItem) {
        mutableStateOf(if (isSmallItem) 4.dp else 8.dp)
    }

    val paddingStart by remember(isSmallItem) {
        mutableStateOf(if (isSmallItem) 8.dp else 20.dp)
    }

    val size by remember(isSmallItem) {
        mutableStateOf(if (isSmallItem) 32.dp else 67.dp)
    }

    val strokeWidth by remember(isSmallItem) {
        mutableStateOf(if (isSmallItem) 1.dp else 2.dp)
    }

    val backgroundIconPadding by remember(isSmallItem) {
        mutableStateOf(if (isSmallItem) 2.dp else 4.dp)
    }

    val centerIcon by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) R.drawable.ic_cancel_small else R.drawable.ic_cancel
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(start = paddingStart, top = paddingTop)
            .size(size)
            .clip(CircleShape)
    ) {
        if (progress != -1f) {
            CircularProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = strokeWidth,
                progress = progress
            )
        } else {
            CircularProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = strokeWidth
            )
        }

        ViraIcon(
            drawable = R.drawable.ic_transparent_circle,
            contentDescription = null,
            tint = Color_Primary,
            modifier = Modifier.fillMaxSize()
        )

        ViraIcon(
            drawable = R.drawable.ic_transparent_circle,
            contentDescription = null,
            tint = Color_Primary,
            modifier = Modifier
                .fillMaxSize()
                .padding(backgroundIconPadding)
                .clip(CircleShape)
        )

        ViraIcon(
            drawable = centerIcon,
            contentDescription = null,
            tint = Color_White
        )
    }
}

@Preview
@Composable
fun Preview() {
    ViraPreview {
        ImazhArchiveListScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}

private fun getNewImageResult(navController: NavHostController): Flow<Boolean>? {
    return navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(KEY_NEW_IMAGE_RESULT, false)
        ?.map { it }
}

private fun resetNewImageResult(navController: NavHostController) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.remove<Boolean>(KEY_NEW_IMAGE_RESULT)
}