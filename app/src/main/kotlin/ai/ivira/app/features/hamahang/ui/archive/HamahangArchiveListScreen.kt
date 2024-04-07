package ai.ivira.app.features.hamahang.ui.archive

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetState
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.archive.DeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ai.ivira.app.features.avasho.ui.archive.model.DownloadingFileStatus
import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangArchiveCheckingFileElement
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangArchiveProcessedFileElement
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangArchiveTrackingFileElement
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangArchiveUploadingFileElement
import ai.ivira.app.features.hamahang.ui.archive.element.HamahangItemImageStatus
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangArchiveView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangCheckingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType.Delete
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType.DeleteConfirmation
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType.FileAccessPermissionDenied
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType.Process
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangFileType.Rename
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangSpeakerView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangTrackingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangUploadingFileView
import ai.ivira.app.features.hamahang.ui.archive.model.convertDate
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangNewAudioResult
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiStatus
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import java.io.File

private const val TRACKING_FILE_ANIMATION_DURATION_COLUMN = 1300

@Composable
fun HamahangArchiveListScreenRoute(
    navController: NavController
) {
    val viewModel = hiltViewModel<HamahangArchiveListViewModel>()

    navController.currentBackStackEntry?.savedStateHandle
        ?.remove<HamahangNewAudioResult>(HamahangNewAudioResult.NEW_FILE_AUDIO_RESULT)
        ?.let {
            viewModel.addFileToChecking(it.inputPath, it.speaker, it.title)
        }

    HamahangArchiveListScreen(
        viewModel = viewModel,
        navigateToDetailScreen = { id ->
            navController.navigate(HamahangScreenRoutes.HamahangDetailScreen.createRoute(id))
        },
        navigateToNewAudio = {
            navController.navigate(HamahangScreenRoutes.HamahangNewAudioScreen.route)
        },
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun HamahangArchiveListScreen(
    viewModel: HamahangArchiveListViewModel,
    navigateToDetailScreen: (id: Int) -> Unit,
    navigateToNewAudio: () -> Unit,
    navigateUp: () -> Unit
) {
    val sheetState = rememberViraBottomSheetState()
    val hamahangListState by rememberLazyListState().isScrollingUp()
    val (fileSheetState, setBottomSheetType) = rememberSaveable {
        mutableStateOf<HamahangFileType>(Process)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val infiniteTransition = rememberInfiniteTransition(label = "columnBrushTransition")
    val archiveList by viewModel.allArchiveFiles.collectAsStateWithLifecycle(listOf())
    val uiViewState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadStatus.collectAsStateWithLifecycle()
    var selectedItem by viewModel.selectedHamahangItem
    val isThereTrackingOrUploading by viewModel.isThereTrackingOrUploading.collectAsStateWithLifecycle()
    val downloadFailureList by viewModel.downloadFailureList.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val writeStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val item = selectedItem
        if (isGranted) {
            // make sure it's Processed and it's not null
            if (item is HamahangProcessedFileView) {
                viewModel.saveToDownloadFolder(
                    filePath = item.filePath,
                    fileName = item.title
                ).also { isSuccess ->

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
    LaunchedEffect(sheetState.isVisible) {
        snackbarHostState.currentSnackbarData?.dismiss()
    }

    HamahangArchiveListUI(
        sheetState = sheetState,
        archiveList = archiveList,
        networkStatus = networkStatus,
        downloadState = downloadState,
        uiViewState = uiViewState,
        sheetContentState = fileSheetState,
        navigateToNewAudio = navigateToNewAudio,
        navigateUp = navigateUp,
        isVisible = hamahangListState,
        scaffoldState = scaffoldState,
        infiniteTransition = infiniteTransition,
        downloadFailureList = downloadFailureList,
        isThereTrackingOrUploading = isThereTrackingOrUploading,
        hasPermissionDenied = viewModel.hasPermissionDeniedPermanently.value,
        selectedItem = selectedItem,
        snackBarAction = {
            snackbarHostState.currentSnackbarData?.dismiss()
            navigateToAppSettings(activity = context as Activity)
        },
        isInDownloadQueue = { id ->
            viewModel.isInDownloadQueue(id)
        },
        onSelectedProcessItemClick = { processItem ->
            if (File(processItem.filePath).exists()) {
                if (!processItem.isSeen) {
                    viewModel.markFileAsSeen(processItem.id)
                }
                navigateToDetailScreen(processItem.id)
            } else {
                if (viewModel.isInDownloadQueue(processItem.id)) {
                    viewModel.cancelDownload(processItem.id)
                } else {
                    viewModel.addFileToDownloadQueue(processItem)
                }
            }
        },
        onProcessItemMenuClick = { processItem ->
            selectedItem = processItem
            setBottomSheetType(Process)
            sheetState.show()
        },
        onTrackingItemMenuClick = { item ->
            selectedItem = item
            setBottomSheetType(Delete)
            sheetState.show()
        },
        onUploadingItemMenuClick = { item ->
            selectedItem = item
            setBottomSheetType(Delete)
            sheetState.show()
        },
        onCheckingItemMenuClick = { checkingItem ->
            selectedItem = checkingItem
            setBottomSheetType(Delete)
            sheetState.show()
        },
        renameAction = { rename ->
            setBottomSheetType(rename)
            sheetState.show()
        },
        onDeleteAction = { delete ->
            setBottomSheetType(delete)
            sheetState.show()
        },
        onConfirmationDeleteAction = {
            when (selectedItem) {
                is HamahangCheckingFileView -> {
                    val checkingItem = selectedItem as HamahangCheckingFileView
                    viewModel.deleteCheckingFile(
                        id = checkingItem.id,
                        filePath = checkingItem.inputFilePath
                    )
                }

                is HamahangProcessedFileView -> {
                    val processItem = selectedItem as HamahangProcessedFileView
                    viewModel.deleteProcessedFile(
                        id = processItem.id,
                        filePath = processItem.filePath
                    )
                }

                is HamahangUploadingFileView -> {
                    viewModel.removeUploadingFile(selectedItem as HamahangUploadingFileView)
                }

                is HamahangTrackingFileView -> {
                    viewModel.deleteTrackingFile((selectedItem as HamahangTrackingFileView).token)
                }
            }
            sheetState.hide()
        },
        onSaveAudioFileClick = onClick@{ title, filePath ->
            if (!isSdkVersionBetween23And29()) {
                viewModel.saveToDownloadFolder(
                    filePath = filePath,
                    fileName = title
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
                    filePath = filePath,
                    fileName = title
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
        downloadAudioFile = { processItem ->
            viewModel.addFileToDownloadQueue(processItem)
        },
        onRenameAction = { id, title ->
            viewModel.updateTitle(title = title, id = id)
            sheetState.hide()
        },
        onShareClick = { filePath ->
            sheetState.hide()
            shareMp3(
                context = context,
                file = File(filePath)
            )
        },
        onTryAgainAction = { uploadItem -> viewModel.startUploading(uploadItem) }
    )
}

@Composable
private fun HamahangArchiveListUI(
    isVisible: Boolean,
    isThereTrackingOrUploading: Boolean,
    hasPermissionDenied: Boolean,
    uiViewState: UiStatus,
    archiveList: List<HamahangArchiveView>,
    downloadFailureList: List<Int>,
    sheetState: ViraBottomSheetState,
    sheetContentState: HamahangFileType,
    networkStatus: NetworkStatus,
    downloadState: DownloadingFileStatus,
    scaffoldState: ScaffoldState,
    infiniteTransition: InfiniteTransition,
    selectedItem: HamahangArchiveView?,
    onConfirmationDeleteAction: () -> Unit,
    navigateToNewAudio: () -> Unit,
    navigateUp: () -> Unit,
    snackBarAction: () -> Unit,
    isInDownloadQueue: (Int) -> Boolean,
    onShareClick: (String) -> Unit,
    onSelectedProcessItemClick: (HamahangProcessedFileView) -> Unit,
    onProcessItemMenuClick: (HamahangProcessedFileView) -> Unit,
    onTrackingItemMenuClick: (HamahangTrackingFileView) -> Unit,
    onUploadingItemMenuClick: (HamahangUploadingFileView) -> Unit,
    onCheckingItemMenuClick: (HamahangCheckingFileView) -> Unit,
    onTryAgainAction: (HamahangUploadingFileView) -> Unit,
    renameAction: (HamahangFileType) -> Unit,
    onDeleteAction: (HamahangFileType) -> Unit,
    onSaveAudioFileClick: (title: String, filePath: String) -> Unit,
    onRenameAction: (id: Int, title: String) -> Unit,
    downloadAudioFile: (HamahangProcessedFileView) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        scaffoldState = scaffoldState,
        snackbarHost = snackBarHost@{ snackBarState ->
            if (hasPermissionDenied) {
                SnackBar(
                    snackbarHostState = snackBarState,
                    paddingBottom = 32.dp,
                    labelAction = snackBarState.currentSnackbarData?.actionLabel,
                    onActionClick = snackBarAction
                )
                return@snackBarHost
            }
            SnackBar(
                snackbarHostState = snackBarState,
                paddingBottom = 32.dp
            )
        },
        topBar = {
            HamahangArchiveAppBar(onBackClick = navigateUp)
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            if (archiveList.isEmpty()) {
                HamahangArchiveEmptyBody()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    val isNetworkAvailable by remember(networkStatus) {
                        mutableStateOf(networkStatus is NetworkStatus.Available)
                    }
                    val hasVpnConnection by remember(networkStatus) {
                        mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
                    }
                    val isBannerError by remember(uiViewState) {
                        mutableStateOf(uiViewState.let { it is UiError && !it.isSnack })
                    }
                    val isFailureDownload by remember(downloadState) {
                        mutableStateOf(downloadState is DownloadingFileStatus.FailureDownload)
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
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(archiveList) {
                            when (it) {
                                is HamahangCheckingFileView -> {
                                    HamahangArchiveCheckingFileElement(
                                        value = it,
                                        isNetworkAvailable = isNetworkAvailable,
                                        isErrorState = false,
                                        onTryAgainClick = {},
                                        onMenuClick = { checkingItem ->
                                            onCheckingItemMenuClick(checkingItem)
                                        }
                                    )
                                }

                                is HamahangProcessedFileView -> HamahangArchiveProcessedFileElement(
                                    archiveViewProcessed = it,
                                    isDownloadFailure = downloadFailureList.contains(it.id),
                                    isInDownloadQueue = isInDownloadQueue(it.id),
                                    isNetworkAvailable = isNetworkAvailable,
                                    onItemClick = { item ->
                                        onSelectedProcessItemClick(item)
                                    },
                                    onMenuClick = { hamahangSelectedItem ->
                                        onProcessItemMenuClick(hamahangSelectedItem)
                                    }
                                )

                                is HamahangTrackingFileView -> {
                                    HamahangArchiveTrackingFileElement(
                                        archiveTrackingView = it,
                                        brush = columnBrush(infiniteTransition),
                                        estimateTime = { it.computeFileEstimateProcess() },
                                        iconItemState = HamahangItemImageStatus.Converting,
                                        onMenuClick = { trackingItem ->
                                            onTrackingItemMenuClick(
                                                trackingItem
                                            )
                                        }
                                    )
                                }

                                is HamahangUploadingFileView -> {
                                    HamahangArchiveUploadingFileElement(
                                        hamahangUploadingFileView = it,
                                        isNetworkAvailable = isNetworkAvailable,
                                        isErrorState = uiViewState.let { uiStatus ->
                                            (uiStatus is UiError) && !uiStatus.isSnack
                                        },
                                        onTryAgainClick = { onTryAgainAction(it) },
                                        onMenuClick = { uploadingItem ->
                                            onUploadingItemMenuClick(
                                                uploadingItem
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HamahangFab(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = navigateToNewAudio,
                isVisible = isVisible
            )
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(
            sheetState = sheetState,
            shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
        ) {
            ViraBottomSheetContent(sheetContentState) {
                val hamahangItem = selectedItem ?: return@ViraBottomSheetContent

                when (sheetContentState) {
                    Process -> {
                        if (hamahangItem is HamahangProcessedFileView) {
                            HamahangProcessedWithDownloadBottomSheet(
                                title = hamahangItem.title,
                                saveAudioFile = {
                                    onSaveAudioFileClick(hamahangItem.title, hamahangItem.filePath)
                                },
                                shareItemAction = { onShareClick(hamahangItem.filePath) },
                                renameItemAction = {
                                    renameAction(Rename)
                                },
                                deleteItemAction = {
                                    onDeleteAction(DeleteConfirmation)
                                },
                                downloadAudioFile = {
                                    downloadAudioFile(hamahangItem)
                                },
                                isFileDownloading = isInDownloadQueue(hamahangItem.id),
                                isFileDownloaded = File(hamahangItem.filePath).exists()
                            )
                        }
                    }
                    Rename -> {
                        if (selectedItem is HamahangProcessedFileView) {
                            RenameFileBottomSheet(
                                fileName = selectedItem.title,
                                shouldShowKeyBoard = true,
                                reNameAction = { name ->
                                    onRenameAction(selectedItem.id, name)
                                }
                            )
                        }
                    }
                    DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = onConfirmationDeleteAction,
                            cancelAction = { sheetState.hide() },
                            fileName = hamahangItem.title
                        )
                    }
                    Delete -> {
                        DeleteBottomSheet(
                            fileName = hamahangItem.title,
                            onDelete = { onDeleteAction(DeleteConfirmation) }
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
private fun HamahangArchiveEmptyBody(
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
                text = stringResource(id = R.string.lbl_dose_not_exist_any_file_yet),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_make_your_first_audio_file),
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
private fun HamahangFab(
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
private fun HamahangArchiveAppBar(
    onBackClick: () -> Unit,
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
            text = stringResource(id = R.string.lbl_hamahang),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
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
            repeatMode = RepeatMode.Reverse
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
private fun HamahangArchiveListUIPreview() {
    ViraPreview {
        HamahangArchiveListUI(
            sheetState = rememberViraBottomSheetState(),
            sheetContentState = Rename,
            archiveList = emptyList(),
            networkStatus = NetworkStatus.Unavailable,
            downloadState = DownloadingFileStatus.IdleDownload,
            uiViewState = UiIdle,
            isVisible = false,
            scaffoldState = rememberScaffoldState(),
            infiniteTransition = rememberInfiniteTransition(label = ""),
            selectedItem = HamahangProcessedFileView(
                id = 0,
                title = "",
                fileUrl = "",
                filePath = "",
                inputFilePath = "",
                speaker = HamahangSpeakerView.Chavoshi,
                createdAt = convertDate(21),
                isSeen = false,
                downloadingPercent = 0f,
                fileDuration = 0,
                fileSize = 0,
                downloadedBytes = 0
            ),
            navigateToNewAudio = {},
            onSelectedProcessItemClick = {},
            onProcessItemMenuClick = {},
            onTrackingItemMenuClick = { },
            onUploadingItemMenuClick = { },
            onCheckingItemMenuClick = {},
            renameAction = {},
            onDeleteAction = {},
            navigateUp = {},
            onConfirmationDeleteAction = {},
            onSaveAudioFileClick = { a, b -> },
            downloadAudioFile = {},
            isInDownloadQueue = { false },
            onRenameAction = { a, b -> },
            isThereTrackingOrUploading = false,
            onTryAgainAction = {},
            onShareClick = {},
            snackBarAction = {},
            hasPermissionDenied = false,
            downloadFailureList = emptyList()
        )
    }
}