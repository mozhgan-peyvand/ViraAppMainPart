package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Expanded
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.Hidden
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetValue.PartiallyExpanded
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.archive.DeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ai.ivira.app.features.avasho.ui.AvashoAnalytics
import ai.ivira.app.features.avasho.ui.AvashoScreenRoutes.AvaShoFileCreationScreen
import ai.ivira.app.features.avasho.ui.AvashoScreenRoutes.AvashoSearchScreen
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.Delete
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.DeleteConfirmation
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.Details
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.FileAccessPermissionDenied
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.Process
import ai.ivira.app.features.avasho.ui.archive.AvashoFileType.Rename
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Converting
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveProcessedFileElement
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveTrackingFileElement
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveUploadingFileElement
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoTrackingFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoUploadingFileView
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus.FailureDownload
import ai.ivira.app.features.avasho.ui.detail.AvashoDetailBottomSheet
import ai.ivira.app.features.avasho.ui.file_creation.SpeechResult
import ai.ivira.app.features.config.ui.ConfigViewModel
import ai.ivira.app.utils.data.NetworkStatus.Available
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.isSdkVersionBetween23And29
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.shareMp3
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.BLue_a200_Opacity_40
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraBannerInfo
import ai.ivira.app.utils.ui.widgets.ViraBannerWithAnimation
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

private const val TRACKING_FILE_ANIMATION_DURATION_COLUMN = 1300
private const val SELECTED_ITEM_KEY = "selectedItemId"

@Composable
fun AvashoArchiveListScreenRoute(navController: NavHostController) {
    val activity = LocalContext.current as ComponentActivity
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(AvashoAnalytics.screenViewArchiveList)
    }

    AvashoArchiveListScreen(
        navController = navController,
        viewModel = hiltViewModel(viewModelStoreOwner = activity),
        configViewModel = hiltViewModel(viewModelStoreOwner = activity)
    )
}

@Composable
private fun AvashoArchiveListScreen(
    navController: NavHostController,
    viewModel: AvashoArchiveListViewModel,
    configViewModel: ConfigViewModel
) {
    LaunchedEffect(Unit) {
        configViewModel.avashoTileConfig.collect { avashoTileConfig ->
            if (avashoTileConfig?.available == false) {
                configViewModel.showAvashoUnavailableFeature()
                navController.navigateUp()
            }
        }
    }

    var selectedFileId by remember { mutableIntStateOf(-1) }
    var selectedFileIndex by remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()
    val infiniteTransition = rememberInfiniteTransition(label = "columnBrushTransition")
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val context = LocalContext.current
    val archiveFiles by viewModel.allArchiveFiles.collectAsStateWithLifecycle(listOf())
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val uiViewState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)
    val downloadState by viewModel.downloadStatus.collectAsStateWithLifecycle()
    val downloadFailureList by viewModel.downloadFailureList.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var progressState by remember { mutableFloatStateOf(0f) }
    var calculatedProgress by remember { mutableFloatStateOf(0f) }
    var bottomSheetTargetValue by rememberSaveable { mutableStateOf(PartiallyExpanded) }
    val isThereTrackingOrUploading by viewModel.isThereTrackingOrUploading.collectAsStateWithLifecycle()
    val isUploadingAllowed by viewModel.isUploadingAllowed.collectAsStateWithLifecycle()

    navController.currentBackStackEntry?.savedStateHandle?.remove<SpeechResult>(SpeechResult.FILE_NAME)
        ?.let {
            viewModel.addToQueue(
                fileName = it.fileName,
                speakerType = it.speakerType,
                text = it.text
            )
        }

    navController.currentBackStackEntry?.savedStateHandle?.get<Int>(SELECTED_ITEM_KEY)?.let { id ->
        selectedFileId = id
    }

    val (fileSheetState, setBottomSheetType) = rememberSaveable {
        mutableStateOf<AvashoFileType>(Details)
    }
    val sheetState = rememberViraBottomSheetState(skipPartiallyExpanded = fileSheetState != Details)

    var bottomSheetCurrentValue by rememberSaveable { mutableStateOf(PartiallyExpanded) }

    var selectedAvashoItem by viewModel.selectedAvashoItemBottomSheet

    var shouldShowSnackBarWithPadding by remember { mutableStateOf(true) }
    val eventHandler = LocalEventHandler.current
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    val writeStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val avashoItem = selectedAvashoItem
        if (isGranted) {
            // make sure it's Processed and it's not null
            if (avashoItem is AvashoProcessedFileView) {
                viewModel.saveToDownloadFolder(
                    filePath = avashoItem.filePath,
                    fileName = avashoItem.title
                ).also { isSuccess ->

                    if (fileSheetState != Details) sheetState.hide()

                    if (isSuccess) {
                        showMessage(
                            snackbarHostState,
                            coroutineScope,
                            context.getString(R.string.msg_file_saved_successfully)
                        )
                    }
                }
            }
        } else {
            if (fileSheetState != Details) sheetState.hide()

            viewModel.putDeniedPermissionToSharedPref(
                permission = permission,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = permission
                )
            )
            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_file_permission)
            )
        }
    }

    val sheetBorderShape by remember(sheetState.currentValue) {
        mutableStateOf(
            if (sheetState.currentValue == PartiallyExpanded) {
                RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
            } else {
                RoundedCornerShape(0.dp)
            }
        )
    }

    LaunchedEffect(archiveFiles.isNotEmpty(), selectedFileId != -1) {
        if (archiveFiles.isNotEmpty() && selectedFileId != -1) {
            val item = viewModel.processArchiveFileList.value.firstOrNull {
                it.id == selectedFileId
            }
            selectedFileIndex = archiveFiles.indexOfFirst {
                it == item
            }
            coroutineScope.launch {
                listState.animateScrollToItem(selectedFileIndex)
            }
        }
    }

    LaunchedEffect(sheetState.targetValue) {
        snapshotFlow { sheetState.targetValue }
            .collect { targetValue ->
                bottomSheetTargetValue = targetValue
                shouldShowSnackBarWithPadding = targetValue == Hidden
            }
    }

    LaunchedEffect(sheetState.currentValue) {
        snapshotFlow { sheetState.currentValue }
            .collect { currentValue ->
                bottomSheetCurrentValue = currentValue
            }
    }

    LaunchedEffect(sheetState.progress) {
        snapshotFlow { sheetState.progress }
            .collect { progress ->
                progressState = progress
            }
    }

    LaunchedEffect(progressState, bottomSheetCurrentValue, bottomSheetTargetValue) {
        val isGoingToBeHalfFromHidden = bottomSheetCurrentValue == Hidden &&
            bottomSheetTargetValue == PartiallyExpanded
        val isGoingToHiddenFromHalf = bottomSheetCurrentValue == PartiallyExpanded &&
            bottomSheetTargetValue == Hidden
        val isGoingToHiddenFromExpand = bottomSheetCurrentValue == Expanded &&
            bottomSheetTargetValue == Hidden
        val isGoingToExpandedFromHafExpanded = bottomSheetCurrentValue == PartiallyExpanded &&
            bottomSheetTargetValue == Expanded
        val isGoingToHalfExpandedFromExpanded = bottomSheetCurrentValue == Expanded &&
            bottomSheetTargetValue == PartiallyExpanded

        calculatedProgress =
            if (isGoingToBeHalfFromHidden || isGoingToHiddenFromHalf || isGoingToHiddenFromExpand) {
                0f
            } else if (isGoingToExpandedFromHafExpanded) {
                progressState
            } else if (isGoingToHalfExpandedFromExpanded) {
                1 - progressState
            } else if (bottomSheetCurrentValue == PartiallyExpanded) {
                0f
            } else if (bottomSheetCurrentValue == Expanded) {
                1f
            } else {
                0f
            }
    }

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                showMessage(
                    snackbarHostState,
                    coroutineScope,
                    it.message
                )
            }
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        snackbarHostState.currentSnackbarData?.dismiss()
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        scaffoldState = scaffoldState,
        topBar = {
            ArchiveAppBar(
                onBackClick = {
                    navController.navigateUp()
                },
                searchEnabled = archiveFiles.isNotEmpty(),
                onSearch = {
                    if (selectedFileId != -1) {
                        selectedFileIndex = -1
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Int>(
                            SELECTED_ITEM_KEY
                        )
                    }
                    navController.navigate(AvashoSearchScreen.route)
                }
            )
        },
        snackbarHost = snackBarHost@{ snackBarState ->

            if (shouldShowSnackBarWithPadding) {
                SnackBarWithPaddingBottom(
                    snackbarHostState = snackBarState,
                    shouldShowOverItems = true,
                    paddingValue = 400f
                )
                return@snackBarHost
            }

            SnackBar(
                snackbarHostState = snackBarState,
                paddingBottom = 32.dp
            )
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            if (archiveFiles.isEmpty()) {
                ArchiveEmptyBody()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    val isNetworkAvailable by remember(networkStatus) {
                        mutableStateOf(networkStatus is Available)
                    }
                    val hasVpnConnection by remember(networkStatus) {
                        mutableStateOf(networkStatus.let { it is Available && it.hasVpn })
                    }
                    val isBannerError by remember(uiViewState) {
                        mutableStateOf(uiViewState.let { it is UiError && !it.isSnack })
                    }
                    val isFailureDownload by remember(downloadState) {
                        mutableStateOf(downloadState is FailureDownload)
                    }

                    ViraBannerWithAnimation(
                        isVisible = (!isNetworkAvailable || hasVpnConnection || isBannerError || isFailureDownload) &&
                            isThereTrackingOrUploading,
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

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(archiveFiles) {
                            when (it) {
                                is AvashoProcessedFileView -> AvashoArchiveProcessedFileElement(
                                    archiveViewProcessed = it,
                                    isDownloadFailure = downloadFailureList.contains(it.id),
                                    isInDownloadQueue = viewModel.isInDownloadQueue(it.id),
                                    isNetworkAvailable = isNetworkAvailable,
                                    isPlaying = viewModel.isItemPlaying(it.id),
                                    onItemClick = callback@{ item ->
                                        if (selectedFileId != -1) {
                                            selectedFileId = -1
                                            navController.currentBackStackEntry?.savedStateHandle?.remove<Int>(
                                                SELECTED_ITEM_KEY
                                            )
                                        }
                                        selectedAvashoItem = item
                                        if (File(item.filePath).exists()) {
                                            eventHandler.specialEvent(AvashoAnalytics.playItem)
                                            setBottomSheetType(Details)
                                            if (!item.isSeen) {
                                                viewModel.markFileAsSeen(item.id)
                                            }
                                            sheetState.show()
                                            return@callback
                                        }

                                        if (downloadFailureList.contains(it.id)) {
                                            viewModel.retryDownload(it.id)
                                        } else if (!viewModel.isInDownloadQueue(it.id)) {
                                            viewModel.addFileToDownloadQueue(item)
                                        } else {
                                            viewModel.cancelDownload(item.id)
                                        }
                                    },
                                    onMenuClick = { avashoSelectedItem ->
                                        selectedAvashoItem = avashoSelectedItem
                                        setBottomSheetType(Process)
                                        sheetState.show()
                                    },
                                    selectedItem = selectedFileId
                                )

                                is AvashoTrackingFileView -> {
                                    AvashoArchiveTrackingFileElement(
                                        archiveTrackingView = it,
                                        brush = columnBrush(infiniteTransition),
                                        estimateTime = { it.computeFileEstimateProcess() },
                                        audioImageStatus = Converting,
                                        onMenuClick = { trackingItem ->
                                            selectedAvashoItem = trackingItem
                                            setBottomSheetType(Delete)
                                            sheetState.show()
                                        }
                                    )
                                }

                                is AvashoUploadingFileView -> {
                                    AvashoArchiveUploadingFileElement(
                                        avashoUploadingFileView = it,
                                        isNetworkAvailable = isNetworkAvailable,
                                        isErrorState = uiViewState.let { uiStatus ->
                                            (uiStatus is UiError) && !uiStatus.isSnack
                                        },
                                        onTryAgainClick = { uploadingItem ->
                                            viewModel.startUploading(uploadingItem)
                                        },
                                        onMenuClick = { uploadingItem ->
                                            selectedAvashoItem = uploadingItem
                                            setBottomSheetType(Delete)
                                            sheetState.show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Fab(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = onClick@{
                    if (!isUploadingAllowed) {
                        val hasError = uiViewState is UiError && isThereTrackingOrUploading
                        showMessage(
                            snackbarHostState,
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

                    navController.navigate(route = AvaShoFileCreationScreen.route)
                },
                listState = listState
            )
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(
            sheetState = sheetState,
            shape = sheetBorderShape
        ) {
            ViraBottomSheetContent(fileSheetState) {
                val avashoItem = selectedAvashoItem ?: return@ViraBottomSheetContent
                when (fileSheetState) {
                    Details -> {
                        if (avashoItem is AvashoProcessedFileView) {
                            AvashoDetailBottomSheet(
                                animationProgress = calculatedProgress,
                                collapseToolbarAction = {
                                    sheetState.hide()
                                },
                                halfToolbarAction = {
                                    sheetState.halfExpand()
                                },
                                changePlayingItemAction = { isPlaying ->
                                    viewModel.changePlayingItem(
                                        if (isPlaying) {
                                            avashoItem.id
                                        } else {
                                            -1
                                        }
                                    )
                                },
                                avashoProcessedItem = avashoItem,
                                isBottomSheetVisible = sheetState.isVisible,
                                isBottomSheetExpanded = sheetState.currentValue == Expanded
                            )
                        }
                    }
                    Process -> {
                        if (avashoItem is AvashoProcessedFileView) {
                            ProcessedWithDownloadBottomSheet(
                                title = avashoItem.title,
                                saveAudioFile = onClick@{
                                    eventHandler.specialEvent(AvashoAnalytics.downloadItem)
                                    if (!isSdkVersionBetween23And29()) {
                                        viewModel.saveToDownloadFolder(
                                            filePath = avashoItem.filePath,
                                            fileName = avashoItem.title
                                        ).also { isSuccess ->
                                            sheetState.hide()

                                            if (isSuccess) {
                                                showMessage(
                                                    snackbarHostState,
                                                    coroutineScope,
                                                    context.getString(R.string.msg_file_saved_successfully)
                                                )
                                            }
                                        }
                                        return@onClick
                                    }

                                    if (context.hasPermission(permission)) {
                                        viewModel.saveToDownloadFolder(
                                            filePath = avashoItem.filePath,
                                            fileName = avashoItem.title
                                        ).also { isSuccess ->

                                            sheetState.hide()

                                            if (isSuccess) {
                                                showMessage(
                                                    snackbarHostState,
                                                    coroutineScope,
                                                    context.getString(R.string.msg_file_saved_successfully)
                                                )
                                            }
                                        }
                                    } else if (viewModel.hasDeniedPermissionPermanently(permission)) {
                                        setBottomSheetType(FileAccessPermissionDenied)
                                        sheetState.show()
                                    } else {
                                        // Asking for permission
                                        writeStoragePermission.launch(permission)
                                    }
                                },
                                shareItemAction = {
                                    eventHandler.specialEvent(AvashoAnalytics.shareItem)
                                    sheetState.hide()

                                    shareMp3(
                                        context = context,
                                        file = File(avashoItem.filePath)
                                    )
                                },
                                renameItemAction = {
                                    setBottomSheetType(Rename)
                                    sheetState.show()
                                },
                                deleteItemAction = {
                                    setBottomSheetType(DeleteConfirmation)
                                    sheetState.show()
                                },
                                downloadAudioFile = {
                                    if (!File(avashoItem.filePath).exists()) {
                                        if (viewModel.isInDownloadQueue(avashoItem.id)) {
                                            viewModel.cancelDownload(avashoItem.id)
                                        } else {
                                            viewModel.addFileToDownloadQueue(avashoItem)
                                        }
                                        sheetState.hide()
                                    }
                                },
                                isFileDownloading = viewModel.isInDownloadQueue(avashoItem.id),
                                isFileDownloaded = File(avashoItem.filePath).exists()

                            )
                        }
                    }
                    Rename -> {
                        if (avashoItem is AvashoProcessedFileView) {
                            RenameFileBottomSheet(
                                fileName = avashoItem.title,
                                shouldShowKeyBoard = true,
                                reNameAction = { name ->
                                    viewModel.updateTitle(
                                        title = name,
                                        id = avashoItem.id
                                    )
                                    sheetState.hide()
                                }
                            )
                        }
                    }
                    DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                when (avashoItem) {
                                    is AvashoProcessedFileView -> {
                                        kotlin.runCatching {
                                            viewModel.removeProcessedFile(avashoItem.id)
                                            File(avashoItem.filePath).delete()
                                        }
                                    }

                                    is AvashoUploadingFileView -> {
                                        eventHandler.specialEvent(AvashoAnalytics.cancelUploadFile)
                                        viewModel.removeUploadingFile(avashoItem.id)
                                    }

                                    is AvashoTrackingFileView -> {
                                        eventHandler.specialEvent(AvashoAnalytics.cancelTrackFile)
                                        viewModel.removeTrackingFile(avashoItem.token)
                                    }
                                }
                                sheetState.hide()
                            },
                            cancelAction = {
                                sheetState.hide()
                            },
                            fileName = avashoItem.title
                        )
                    }
                    Delete -> {
                        DeleteBottomSheet(
                            fileName = avashoItem.title,
                            onDelete = {
                                setBottomSheetType(DeleteConfirmation)
                                sheetState.show()
                            }
                        )
                    }
                    FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(
                            cancelAction = {
                                sheetState.hide()
                            },
                            submitAction = {
                                navigateToAppSettings(activity = context as Activity)
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
private fun ArchiveAppBar(
    searchEnabled: Boolean,
    onBackClick: () -> Unit,
    onSearch: () -> Unit,
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
            text = stringResource(id = R.string.lbl_ava_sho),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        if (searchEnabled) {
            IconButton(
                onClick = {
                    safeClick {
                        onSearch()
                    }
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_search,
                    contentDescription = stringResource(id = R.string.desc_search),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun Fab(
    listState: LazyListState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible by listState.isScrollingUp()
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
private fun columnBrush(infiniteTransition: InfiniteTransition): Brush {
    val offset by infiniteTransition.animateFloat(
        initialValue = 0.01f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = TRACKING_FILE_ANIMATION_DURATION_COLUMN,
                easing = EaseInOut
            ),
            repeatMode = Reverse
        ),
        label = "offsetAnimation"
    )

    return remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height
                return LinearGradientShader(
                    colors = listOf(BLue_a200_Opacity_40, Color_Card),
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, size.height),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun AvashoArchiveListScreenPreview() {
    ViraPreview {
        AvashoArchiveListScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel(),
            configViewModel = hiltViewModel()
        )
    }
}