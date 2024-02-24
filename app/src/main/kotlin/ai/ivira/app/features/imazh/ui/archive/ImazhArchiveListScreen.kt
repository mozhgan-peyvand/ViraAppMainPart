package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.archive.DeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RegenerateItemConfirmationBottomSheet
import ai.ivira.app.features.config.ui.ConfigViewModel
import ai.ivira.app.features.imazh.ui.ImazhAnalytics
import ai.ivira.app.features.imazh.ui.ImazhScreenRoutes.ImazhDetailsScreen
import ai.ivira.app.features.imazh.ui.ImazhScreenRoutes.ImazhNewImageDescriptorScreen
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.Delete
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.DeleteConfirmation
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.FileAccessPermissionDenied
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.RegenerateImageConfirmation
import ai.ivira.app.features.imazh.ui.archive.ImazhArchiveBottomSheetType.SelectionModeDeleteConfirmation
import ai.ivira.app.features.imazh.ui.archive.model.ImazhArchiveView
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.archive.model.ImazhTrackingFileView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.KEY_NEW_IMAGE_RESULT
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.OnLifecycleEvent
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.computeSecondAndMinute
import ai.ivira.app.utils.ui.computeTextBySecondAndMinute
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.hide
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.isSdkVersionBetween23And29
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_BG_Imazh_Tracking_Text
import ai.ivira.app.utils.ui.theme.Color_Background_Menu
import ai.ivira.app.utils.ui.theme.Color_Blue_Grey_800_945
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.toBitmap
import ai.ivira.app.utils.ui.widgets.ViraBannerInfo
import ai.ivira.app.utils.ui.widgets.ViraBannerWithAnimation
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImazhArchiveListScreenRoute(navController: NavHostController) {
    val activity = LocalContext.current as ComponentActivity
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(ImazhAnalytics.screenViewArchiveList)
    }

    ImazhArchiveListScreen(
        navController = navController,
        viewModel = hiltViewModel(),
        configViewModel = hiltViewModel(viewModelStoreOwner = activity)
    )
}

@Composable
private fun ImazhArchiveListScreen(
    navController: NavHostController,
    viewModel: ImazhArchiveListViewModel,
    configViewModel: ConfigViewModel
) {
    val context = LocalContext.current
    val snackBarState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarState)
    val coroutineScope = rememberCoroutineScope()
    val isGrid by viewModel.isGrid.collectAsState()
    val archiveFiles by viewModel.allArchiveFiles.collectAsStateWithLifecycle()
    val filesInSelection by viewModel.filesInSelection.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val uiViewState by viewModel.uiViewState.collectAsState(UiIdle)
    val downloadFailureList by viewModel.downloadFailureList.collectAsStateWithLifecycle()
    var isVisible by rememberSaveable { mutableStateOf(true) }
    val isScrollingUp by listState.isScrollingUp()
    val isTrackingEmpty by viewModel.isTrackingEmpty.collectAsStateWithLifecycle()
    val isDownloadQueueEmpty by viewModel.isDownloadQueueEmpty.collectAsStateWithLifecycle()
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
    val selectedMenuItem = remember { mutableStateOf<ImazhArchiveView?>(null) }
    var selectedFilePathDownloadItem by remember { mutableStateOf<String?>(null) }

    val selectedItemIds by viewModel.selectedItemIds
    val isSelectionMode by viewModel.isSelectionMode
    val allItemsAreSelected by remember(selectedItemIds, filesInSelection) {
        derivedStateOf { filesInSelection.size == selectedItemIds.size }
    }

    val newImageResult = getNewImageResult(navController)?.collectAsState(initial = false)
    LaunchedEffect(newImageResult, isScrolledDown) {
        if (newImageResult?.value == true && isScrolledDown) {
            listState.scrollToItem(0)
            resetNewImageResult(navController)
        }
    }

    LaunchedEffect(isGrid) { isVisible = true }
    LaunchedEffect(isScrollingUp) { isVisible = isScrollingUp }
    LaunchedEffect(Unit) {
        configViewModel.imazhTileConfig.collect { imazhTileConfig ->
            if (imazhTileConfig?.available == false) {
                configViewModel.showImazhUnavailableFeature()
                navController.navigateUp()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                modalBottomSheetState.hide(coroutineScope)
                delay(100)
                showMessage(
                    snackBarState,
                    coroutineScope,
                    it.message
                )
            }
        }
    }

    BackHandler(isSelectionMode || modalBottomSheetState.isVisible) {
        if (modalBottomSheetState.isVisible) {
            modalBottomSheetState.hide(coroutineScope)
        } else if (isSelectionMode) {
            viewModel.clearSelectionMode()
        }
    }

    OnLifecycleEvent(
        onResume = {
            if (viewModel.isSharingFiles.value) {
                viewModel.deselectAll()
                viewModel.clearSelectionMode()
            }
            viewModel.setIsSharing(false)
        }
    )
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val writeStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            selectedFilePathDownloadItem?.let { filePath ->
                viewModel.saveToDownloadFolder(
                    filePath = filePath,
                    fileName = File(filePath).nameWithoutExtension
                ).also { isSuccess ->
                    if (isSuccess) {
                        showMessage(
                            snackBarState,
                            coroutineScope,
                            context.getString(R.string.msg_file_saved_successfully)
                        )
                    }
                }
            }
        } else {
            viewModel.putDeniedPermissionToSharedPref(
                permission = permission,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = permission
                )
            )
            showMessage(
                snackBarState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_file_permission)
            )
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = {
            SnackBarWithPaddingBottom(
                snackbarHostState = snackBarState,
                shouldShowOverItems = true,
                paddingValue = if (isVisible) 400f else 150f
            )
        },
        topBar = {
            Crossfade(
                targetState = isSelectionMode,
                animationSpec = tween(200),
                label = "TopBar"
            ) { isSelectionModeOn ->
                if (isSelectionModeOn) {
                    val noOperationMessage = stringResource(id = R.string.msg_no_item_selected)
                    ImazhSelectionModeAppBar(
                        onSelectAllClick = {
                            if (allItemsAreSelected) {
                                viewModel.deselectAll()
                            } else {
                                viewModel.selectAll()
                            }
                        },
                        onDeleteClick = {
                            if (selectedItemIds.isNotEmpty()) {
                                coroutineScope.launch {
                                    selectedSheet = SelectionModeDeleteConfirmation
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    }
                                }
                            } else {
                                showMessage(snackBarState, coroutineScope, noOperationMessage)
                            }
                        },
                        onShareClick = {
                            if (selectedItemIds.isNotEmpty()) {
                                viewModel.shareSelectedItems(context)
                            } else {
                                showMessage(snackBarState, coroutineScope, noOperationMessage)
                            }
                        },
                        onCancelClick = {
                            viewModel.deselectAll()
                            viewModel.clearSelectionMode()
                        },
                        allItemsAreSelected = allItemsAreSelected,
                        isSharingEnabled = !viewModel.isDeletingFiles.value,
                        isClickable = !(selectedSheet == SelectionModeDeleteConfirmation && modalBottomSheetState.isVisible),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                } else {
                    ImazhArchiveAppBar(
                        onBackClick = navController::navigateUp,
                        isGrid = isGrid,
                        showListTypeIcon = archiveFiles.isNotEmpty(),
                        onChangeListTypeClick = {
                            viewModel.saveListType(!isGrid)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
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
                                coroutineScope.launch {
                                    selectedSheet = DeleteConfirmation
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
                                when (info) {
                                    is ImazhTrackingFileView -> {
                                        viewModel.removeTrackingFile(info.token)
                                    }

                                    is ImazhProcessedFileView -> {
                                        viewModel.removeProcessedFile(info.id, info.filePath)
                                    }
                                }

                                modalBottomSheetState.hide(coroutineScope)
                            },
                            cancelAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            fileName = ""
                        )
                    }

                    SelectionModeDeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                coroutineScope.launch(IO) {
                                    viewModel.deleteSelectedItems()
                                }
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            cancelAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            fileName = stringResource(id = R.string.lbl_selected_files)
                        )
                    }

                    FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(
                            cancelAction = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            submitAction = {
                                navigateToAppSettings(activity = context as Activity)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    RegenerateImageConfirmation -> {
                        RegenerateItemConfirmationBottomSheet(
                            cancelAction = {
                                viewModel.resetItemIdForRegenerate()
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            regenerateAction = {
                                viewModel.regenerateImage {
                                    modalBottomSheetState.hide(coroutineScope)
                                }
                            },
                            isLoading = viewModel.isRegeneratingImage.value
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
                        val shouldShowBanner by remember(isTrackingEmpty, isDownloadQueueEmpty) {
                            mutableStateOf(!isTrackingEmpty || !isDownloadQueueEmpty)
                        }
                        ViraBannerWithAnimation(
                            // FIXME: Should this be displaying only if upload is in progress?
                            isVisible = shouldShowBanner &&
                                !isSelectionMode &&
                                (!isNetworkAvailable || hasVpnConnection || isBannerError),
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
                            archiveFiles = if (isSelectionMode) filesInSelection else archiveFiles,
                            listState = listState,
                            isGrid = isGrid,
                            failureList = downloadFailureList,
                            isInDownloadQueue = { id -> viewModel.isInDownloadQueue(id) },
                            onProcessedItemClick = { id ->
                                navController.navigate(ImazhDetailsScreen.createRoute(id))
                            },
                            onMenuClick = { imazhArchiveView ->
                                selectedMenuItem.value = imazhArchiveView
                                coroutineScope.launch {
                                    selectedSheet = Delete
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    }
                                }
                            },
                            onProcessedItemLongClick = { id ->
                                viewModel.enableSelectionMode()
                                viewModel.selectDeselectItems(id)
                            },
                            isSelectionMode = isSelectionMode,
                            selectedItemIds = selectedItemIds,
                            onTryAgainClick = { processedItem ->
                                viewModel.startDownloading(processedItem)
                            },
                            onDownloadAction = onClick@{ imazhFilePathItem ->
                                selectedFilePathDownloadItem = imazhFilePathItem
                                selectedFilePathDownloadItem?.let { filePath ->
                                    if (!isSdkVersionBetween23And29()) {
                                        viewModel.saveToDownloadFolder(
                                            filePath = filePath,
                                            fileName = File(filePath).nameWithoutExtension
                                        ).also { isSuccess ->
                                            if (isSuccess) {
                                                showMessage(
                                                    snackBarState,
                                                    coroutineScope,
                                                    context.getString(R.string.msg_file_saved_successfully)
                                                )
                                            }
                                        }
                                        return@onClick
                                    }
                                    if (context.hasPermission(permission)) {
                                        viewModel.saveToDownloadFolder(
                                            filePath = filePath,
                                            fileName = File(filePath).nameWithoutExtension
                                        ).also { isSuccess ->
                                            if (isSuccess) {
                                                showMessage(
                                                    snackBarState,
                                                    coroutineScope,
                                                    context.getString(R.string.msg_file_saved_successfully)
                                                )
                                            }
                                        }
                                    } else if (viewModel.hasDeniedPermissionPermanently(permission)) {
                                        coroutineScope.launch {
                                            selectedSheet = FileAccessPermissionDenied
                                            modalBottomSheetState.hide()
                                            if (!modalBottomSheetState.isVisible) {
                                                modalBottomSheetState.show()
                                            }
                                        }
                                    } else {
                                        // Asking for permission
                                        writeStoragePermission.launch(permission)
                                    }
                                }
                            },
                            onRegenerateImageClick = { itemId ->
                                if (!isTrackingEmpty) {
                                    val hasError = uiViewState is UiError
                                    showMessage(
                                        snackBarState,
                                        coroutineScope,
                                        context.getString(
                                            if (hasError) {
                                                R.string.msg_wait_for_connection_to_server
                                            } else {
                                                R.string.msg_wait_process_finish_or_cancel_it
                                            }
                                        )
                                    )
                                } else {
                                    viewModel.setItemIdForRegenerate(itemId)
                                    coroutineScope.launch {
                                        selectedSheet = RegenerateImageConfirmation
                                        modalBottomSheetState.hide()
                                        if (!modalBottomSheetState.isVisible) {
                                            modalBottomSheetState.show()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (!isSelectionMode) {
                    ImazhArchiveFab(
                        modifier = Modifier.align(Alignment.BottomStart),
                        onClick = onClick@{
                            if (!isTrackingEmpty) {
                                val hasError = uiViewState is UiError
                                showMessage(
                                    snackBarState,
                                    coroutineScope,
                                    context.getString(
                                        if (hasError) {
                                            R.string.msg_wait_for_connection_to_server
                                        } else {
                                            R.string.msg_wait_process_finish_or_cancel_it
                                        }
                                    )
                                )
                                return@onClick
                            }

                            navController.navigate(route = ImazhNewImageDescriptorScreen.route)
                        },
                        isVisible = isVisible
                    )
                }
            }
        }
    }
}

@Composable
fun ImazhSelectionModeAppBar(
    onSelectAllClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onCancelClick: () -> Unit,
    isSharingEnabled: Boolean,
    isClickable: Boolean,
    allItemsAreSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(targetValue = if (isClickable) 1f else 0.6f, label = "alpha")

    Row(
        modifier = modifier.alpha(alpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (allItemsAreSelected) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                        .background(
                            color = Color_Primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            enabled = isClickable,
                            onClick = onSelectAllClick
                        )
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_selected,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = stringResource(id = R.string.desc_back)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                        .border(
                            width = 1.dp,
                            color = Color_White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            enabled = isClickable,
                            onClick = onSelectAllClick
                        )
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_imazh_select_all),
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.clickable(
                    enabled = isClickable,
                    onClick = onSelectAllClick
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            enabled = isClickable,
            onClick = {
                safeClick {
                    onDeleteClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.icon_trash_delete,
                contentDescription = stringResource(R.string.lbl_btn_delete),
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                tint = Color_White
            )
        }

        IconButton(
            enabled = isClickable && isSharingEnabled,
            onClick = {
                safeClick {
                    onShareClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_share_new,
                contentDescription = stringResource(R.string.lbl_share_file),
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
                tint = Color_White
            )
        }

        IconButton(
            enabled = isClickable,
            onClick = {
                safeClick {
                    onCancelClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_close_new,
                contentDescription = stringResource(R.string.lbl_cancel),
                modifier = Modifier
                    .padding(12.dp)
                    .size(14.dp), // fixme: Icon size is not similar to other appBar icons
                tint = Color_White
            )
        }
    }
}

@Composable
private fun ArchiveListContent(
    archiveFiles: List<ImazhArchiveView>,
    listState: LazyGridState,
    isInDownloadQueue: (Int) -> Boolean,
    isGrid: Boolean,
    isSelectionMode: Boolean,
    failureList: List<Int>,
    selectedItemIds: Set<Int>,
    onProcessedItemClick: (Int) -> Unit,
    onMenuClick: (ImazhArchiveView) -> Unit,
    onProcessedItemLongClick: (Int) -> Unit,
    onTryAgainClick: (ImazhProcessedFileView) -> Unit,
    onDownloadAction: (String) -> Unit,
    onRegenerateImageClick: (Int) -> Unit,
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
                    is ImazhTrackingFileView -> item.token
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
                        isError = failureList.contains(item.id),
                        onClick = if (isSelectionMode) {
                            onProcessedItemLongClick
                        } else {
                            onProcessedItemClick
                        },
                        onMenuClick = onMenuClick,
                        isSelected = selectedItemIds.contains(item.id),
                        showPrompt = !isGrid || !isSelectionMode,
                        onItemLongClick = onProcessedItemLongClick,
                        isSelectionMode = isSelectionMode,
                        onTryAgainClick = { processedItem -> onTryAgainClick(processedItem) },
                        onDownloadAction = { filePath ->
                            onDownloadAction(filePath)
                        },
                        onRegenerateImageClick = { itemId -> onRegenerateImageClick(itemId) }
                    )
                }

                is ImazhTrackingFileView -> {
                    ImazhTrackingItem(
                        item = item,
                        isSmallItem = isGrid,
                        estimateTime = { item.computeFileEstimateProcess() },
                        onMenuClick = { trackingItem -> onMenuClick(trackingItem) }
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
        modifier = modifier,
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
    isSelected: Boolean,
    isSelectionMode: Boolean,
    showPrompt: Boolean,
    isError: Boolean,
    isInDownloadQueue: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onMenuClick: (ImazhProcessedFileView) -> Unit,
    onItemLongClick: (Int) -> Unit,
    onTryAgainClick: (ImazhProcessedFileView) -> Unit,
    onDownloadAction: (String) -> Unit,
    onRegenerateImageClick: (Int) -> Unit,
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
            .combinedClickable(
                enabled = !isInQueue,
                onClick = {
                    safeClick {
                        onClick(item.id)
                    }
                },
                onLongClick = {
                    safeClick {
                        onItemLongClick(item.id)
                    }
                }
            )
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
        } else {
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

            if (isError) {
                BodyError(
                    isSmallItem = isSmallItem,
                    onTryAgainClick = { onTryAgainClick(item) },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
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

        if (showPrompt) {
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
        }

        if (isSelectionMode) {
            SelectBox(
                isSelected = isSelected,
                isSmallItem = isSmallItem
            )
        } else {
            if (!isInQueue) {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = if (isSmallItem) 4.dp else 8.dp,
                            horizontal = if (isSmallItem) 4.dp else 8.dp
                        )
                        .fillMaxWidth(if (isSmallItem) 0.5f else 0.32f)
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(if (isSmallItem) 4.dp else 8.dp)
                ) {
                    RegenerateBox(
                        isSmallItem = isSmallItem,
                        onClickAction = { onRegenerateImageClick(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                    DownloadImage(
                        isSmallItem = isSmallItem,
                        onDownloadAction = { onDownloadAction(item.filePath) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadImage(
    isSmallItem: Boolean,
    onDownloadAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                RoundedCornerShape(16.dp)
            } else {
                RoundedCornerShape(18.dp)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(
                color = Color_Blue_Grey_800_945.copy(alpha = .75f),
                shape = shape
            )
            .clip(shape)
            .clickable { onDownloadAction() }
            .padding(10.dp)
    ) {
        ViraIcon(
            drawable = R.drawable.ic_download_audio,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun RegenerateBox(
    isSmallItem: Boolean,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                RoundedCornerShape(16.dp)
            } else {
                RoundedCornerShape(18.dp)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(
                color = Color_Blue_Grey_800_945.copy(alpha = .75f),
                shape = shape
            )
            .clip(shape)
            .clickable { onClickAction() }
            .padding(10.dp)
    ) {
        ViraIcon(
            drawable = R.drawable.ic_regenerate,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun BoxScope.SelectBox(
    isSmallItem: Boolean,
    isSelected: Boolean
) {
    val shape by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                RoundedCornerShape(16.dp)
            } else {
                RoundedCornerShape(18.dp)
            }
        )
    }

    val verticalPadding by remember(isSmallItem) { mutableStateOf(if (isSmallItem) 4.dp else 8.dp) }

    val horizontalPadding by remember(isSmallItem) { mutableStateOf(if (isSmallItem) 4.dp else 8.dp) }

    Box(
        modifier = Modifier
            .padding(vertical = verticalPadding, horizontal = horizontalPadding)
            .fillMaxWidth(if (isSmallItem) 0.24f else 0.15f)
            .aspectRatio(1f)
            .align(Alignment.TopStart)
            .background(
                color = if (isSelected) Color_Primary else Color_Card_Stroke,
                shape = shape
            )
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color_Card_Stroke,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
    ) {
        if (isSelected) {
            ViraIcon(
                drawable = R.drawable.ic_selected,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ImazhTrackingItem(
    item: ImazhTrackingFileView,
    isSmallItem: Boolean,
    estimateTime: () -> Double,
    onMenuClick: (ImazhTrackingFileView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val textPaddingModifier by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            } else {
                Modifier.padding(vertical = 12.dp, horizontal = 22.dp)
            }
        )
    }

    val lottieSizeModifier by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                Modifier.size(width = 35.dp, height = 43.dp)
            } else {
                Modifier.size(width = 73.dp, height = 90.dp)
            }
        )
    }

    val body1Style = MaterialTheme.typography.body1
    val body2Style = MaterialTheme.typography.body2
    val textStyle by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                body2Style
            } else {
                body1Style
            }
        )
    }

    val iconSizeModifier by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                Modifier.size(14.dp)
            } else {
                Modifier.size(21.dp)
            }
        )
    }

    val paddingBetweenLottieAndText by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                8.dp
            } else {
                16.dp
            }
        )
    }

    val menuButtonSize by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                30.dp
            } else {
                42.dp
            }
        )
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_puzzle)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = true
    )

    val getNewEstimateTime = remember(item.token, item.lastFailure) {
        mutableIntStateOf(estimateTime().toInt())
    }

    val isEstimatedTimeRemains by remember(getNewEstimateTime.intValue) {
        mutableStateOf(getNewEstimateTime.intValue > 0)
    }

    val subtitleText by remember(isEstimatedTimeRemains) {
        mutableIntStateOf(
            if (isEstimatedTimeRemains) {
                R.string.lbl_generating_image
            } else {
                R.string.lbl_processing_queue
            }
        )
    }

    DecreaseEstimateTime(
        estimationTime = getNewEstimateTime.intValue,
        token = item.token
    ) { long ->
        getNewEstimateTime.intValue = long
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color_Card)
    ) {
        IconButton(
            onClick = {
                safeClick { onMenuClick(item) }
            },
            modifier = Modifier
                .padding(end = 8.dp, top = 8.dp)
                .size(menuButtonSize)
                .align(Alignment.TopEnd)
                .background(Color_Background_Menu, RoundedCornerShape(12.dp))
        ) {
            ViraImage(
                drawable = R.drawable.ic_dots_menu,
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)

            ) {
                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = lottieSizeModifier
                )

                Spacer(modifier = Modifier.size(paddingBetweenLottieAndText))

                Text(
                    text = stringResource(id = subtitleText),
                    color = Color_Text_2,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(4.dp))

                if (isEstimatedTimeRemains) {
                    Row {
                        ViraIcon(
                            drawable = R.drawable.ic_time,
                            contentDescription = null,
                            modifier = Modifier
                                .then(iconSizeModifier)
                                .align(alignment = Alignment.CenterVertically),
                            tint = Color_Primary_300
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        Text(
                            text = buildString {
                                append(stringResource(id = R.string.lbl_approximate))
                                append(" ")
                                append(computeSecondAndMinute(getNewEstimateTime.intValue))
                                append(" ")
                                append(
                                    computeTextBySecondAndMinute(
                                        second = getNewEstimateTime.intValue,
                                        context = context
                                    )
                                )
                            },
                            color = Color_Text_2,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text = item.prompt,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color_BG_Imazh_Tracking_Text)
                    .then(textPaddingModifier)
                    .zIndex(1f)
            )
        }
    }
}

@Composable
fun BodyError(
    isSmallItem: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacerSize by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) 8.dp else 24.dp
        )
    }

    val h6TextStyle = MaterialTheme.typography.h6
    val captionTextStyle = MaterialTheme.typography.caption

    val textStyle by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) captionTextStyle else h6TextStyle
        )
    }

    val buttonContentPadding by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                PaddingValues(top = 8.dp, bottom = 8.dp, start = 28.dp, end = 24.dp)
            } else {
                PaddingValues(top = 16.dp, bottom = 16.dp, start = 40.dp, end = 36.dp)
            }

        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.msg_internet_connection_problem),
            style = textStyle,
            color = Color_Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(spacerSize))

        TextButton(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color_Primary_Opacity_15
            ),
            contentPadding = buttonContentPadding,
            shape = RoundedCornerShape(8.dp),
            onClick = {
                safeClick {
                    onTryAgainClick()
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.lbl_try_again),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300
                )

                Spacer(modifier = Modifier.size(8.dp))

                ViraIcon(
                    drawable = R.drawable.ic_retry,
                    contentDescription = null,
                    tint = Color_Primary_300,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Duplicate DecreaseEstimateTime 2
@Composable
fun DecreaseEstimateTime(
    estimationTime: Int,
    token: String,
    callBack: (Int) -> Unit
) {
    val getEstimationTime = remember(token) {
        mutableIntStateOf(estimationTime)
    }
    LaunchedEffect(token) {
        while (getEstimationTime.intValue > 0) {
            if (getEstimationTime.intValue < 14) {
                delay(1000)
                getEstimationTime.intValue -= 1
                callBack(getEstimationTime.intValue)
                continue
            }

            delay(10000)
            getEstimationTime.intValue -= 10
            callBack(getEstimationTime.intValue)
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
        mutableIntStateOf(
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
            viewModel = hiltViewModel(), configViewModel = hiltViewModel()
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