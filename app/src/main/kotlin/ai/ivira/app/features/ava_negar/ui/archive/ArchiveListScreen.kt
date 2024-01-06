package ai.ivira.app.features.ava_negar.ui.archive

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.AvanegarSentry
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics.AvanegarFileType.Processed
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics.AvanegarFileType.Tracking
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics.AvanegarFileType.Uploading
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveProcessedFileElementColumn
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveProcessedFileElementGrid
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveTrackingFileElementGrid
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveTrackingFileElementsColumn
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveUploadingFileElementColumn
import ai.ivira.app.features.ava_negar.ui.archive.element.ArchiveUploadingFileElementGrid
import ai.ivira.app.features.ava_negar.ui.archive.model.ArchiveView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarTrackingFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarUploadingFileView
import ai.ivira.app.features.ava_negar.ui.archive.model.UploadingFileStatus
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenMicrophoneBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.ChooseFileContentBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.DetailItemBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RenameFileContentBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.ShareDetailItemBottomSheet
import ai.ivira.app.features.ava_negar.ui.details.TIME_INTERVAL
import ai.ivira.app.features.ava_negar.ui.record.RecordFileResult
import ai.ivira.app.features.ava_negar.ui.record.RecordFileResult.Companion.FILE_NAME
import ai.ivira.app.features.ava_negar.ui.update.ForceUpdateScreen
import ai.ivira.app.utils.common.event.ViraEvent
import ai.ivira.app.utils.common.file.convertTextToPdf
import ai.ivira.app.utils.common.file.convertTextToTXTFile
import ai.ivira.app.utils.common.file.filename
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.hasRecordAudioPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.isSdkVersion33orHigher
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.openAudioSelector
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.sharePdf
import ai.ivira.app.utils.ui.shareTXT
import ai.ivira.app.utils.ui.shareText
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.BLue_a200_Opacity_40
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.widgets.ViraBannerInfo
import ai.ivira.app.utils.ui.widgets.ViraBannerWithAnimation
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val TRACKING_FILE_ANIMATION_DURATION_Column = 1300
const val TRACKING_FILE_ANIMATION_DURATION_Grid = 1500

@Composable
fun AvaNegarArchiveListScreenRoute(navController: NavHostController) {
    val activity = LocalContext.current as ComponentActivity
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(AvanegarAnalytics.screenViewArchiveList)
    }

    AvaNegarArchiveListScreen(
        navHostController = navController,
        archiveListViewModel = hiltViewModel(viewModelStoreOwner = activity)
    )
}

@Composable
private fun AvaNegarArchiveListScreen(
    navHostController: NavHostController,
    archiveListViewModel: ArchiveListViewModel
) {
    val eventHandler = LocalEventHandler.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val localClipBoardManager = LocalClipboardManager.current

    var isConvertingPdf by rememberSaveable { mutableStateOf(false) }
    var isConvertingTxt by rememberSaveable { mutableStateOf(false) }
    var shouldSharePdf by rememberSaveable { mutableStateOf(false) }
    var shouldShareTxt by rememberSaveable { mutableStateOf(false) }
    var fileName by rememberSaveable { mutableStateOf<String?>("") }
    var fileUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val isGrid by archiveListViewModel.isGrid.collectAsStateWithLifecycle()
    val uploadingId by archiveListViewModel.uploadingId.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isVisible by listState.isScrollingUp()

    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(
            ArchiveBottomSheetType.ChooseFile
        )
    }
    val isAnyBottomSheetOtherThanUpdate by rememberSaveable(selectedSheet) {
        mutableStateOf(selectedSheet != ArchiveBottomSheetType.Update)
    }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { !isConvertingPdf && !isConvertingTxt }
    )

    val modalBottomSheetStateUpdate = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { false }
    )

    val uploadingFileState by archiveListViewModel.isUploading.collectAsStateWithLifecycle(
        UploadingFileStatus.Idle
    )

    val archiveFiles by archiveListViewModel.allArchiveFiles.collectAsStateWithLifecycle(listOf())
    val isThereAnyTrackingOrUploading by archiveListViewModel.isThereAnyTrackingOrUploadingOrFailure.collectAsStateWithLifecycle()
    val isUploadingAllowed by archiveListViewModel.isUploadingAllowed.collectAsStateWithLifecycle()
    val failureList by archiveListViewModel.failureList.collectAsStateWithLifecycle()

    val networkStatus by archiveListViewModel.networkStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    val uiViewState by archiveListViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

    // to ensure errors are received must be here!! (errors of invalid file from either source)
    LaunchedEffect(Unit) {
        archiveListViewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                showMessage(snackbarHostState, this, it.message)
            }
        }
    }

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            coroutineScope.launch {
                if (archiveListViewModel.checkIfUriDurationIsOk(context, it.data?.data)) {
                    setSelectedSheet(ArchiveBottomSheetType.RenameUploading)
                    coroutineScope.launch {
                        if (!modalBottomSheetState.isVisible) {
                            modalBottomSheetState.show()
                        } else {
                            modalBottomSheetState.hide()
                        }
                    }
                    try {
                        fileName =
                            it.data?.data?.filename(context).orEmpty()
                        fileUri = it.data?.data
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }
    val chooseAudioPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchOpenFile.launch(openAudioSelector())
        } else {
            val permission = if (isSdkVersion33orHigher()) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            archiveListViewModel.putDeniedPermissionToSharedPref(
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

    val recordAudioPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            gotoRecordAudioScreen(navHostController)
        } else {
            archiveListViewModel.putDeniedPermissionToSharedPref(
                permission = Manifest.permission.RECORD_AUDIO,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = Manifest.permission.RECORD_AUDIO
                )
            )

            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_to_record_audio_permission)
            )
        }
    }

    var backPressedInterval by remember {
        mutableLongStateOf(0)
    }

    BackHandler(modalBottomSheetStateUpdate.isVisible) {
        // we want to disable back
    }

    BackHandler(modalBottomSheetState.isVisible && !modalBottomSheetStateUpdate.isVisible) {
        if (modalBottomSheetState.isVisible) {
            coroutineScope.launch {
                if (modalBottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
                    coroutineScope.launch(IO) {
                        if (!isConvertingPdf && !isConvertingTxt) {
                            modalBottomSheetState.hide()
                        } else {
                            if (backPressedInterval + TIME_INTERVAL < System.currentTimeMillis()) {
                                withContext(Main) {
                                    Toast.makeText(
                                        context,
                                        context.getString(
                                            R.string.msg_back_again_to_cancel_converting
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                backPressedInterval = System.currentTimeMillis()
                            } else {
                                withContext(Main) {
                                    isConvertingPdf = false
                                    isConvertingTxt = false
                                    modalBottomSheetState.hide()
                                }
                            }
                        }
                    }
                } else {
                    navHostController.navigateUp()
                }
            }
        }
    }

    val shouldShowKeyBoard = rememberSaveable { mutableStateOf(false) }
    val shouldShowKeyBoardUploadingName = rememberSaveable { mutableStateOf(false) }

    navHostController.currentBackStackEntry
        ?.savedStateHandle?.remove<RecordFileResult>(FILE_NAME)?.let {
            archiveListViewModel.addFileToUploadingQueue(it.title, Uri.fromFile(File(it.filepath)))
        }

    LaunchedEffect(modalBottomSheetState.currentValue) {
        if (modalBottomSheetState.isVisible) {
            if (selectedSheet.name == ArchiveBottomSheetType.Rename.name) {
                shouldShowKeyBoard.value = true
            }

            if (selectedSheet.name == ArchiveBottomSheetType.RenameUploading.name) {
                shouldShowKeyBoardUploadingName.value = true
            }
        } else {
            shouldShowKeyBoard.value = false
            shouldShowKeyBoardUploadingName.value = false
        }
    }

    LaunchedEffect(archiveListViewModel.aiEvent.value) {
        if (archiveListViewModel.aiEvent.value == ViraEvent.TokenExpired) {
            setSelectedSheet(ArchiveBottomSheetType.Update)
            modalBottomSheetStateUpdate.show()
        }
    }

    LaunchedEffect(isConvertingPdf) {
        if (isConvertingPdf) {
            archiveListViewModel.jobConverting?.cancel()
            archiveListViewModel.jobConverting = coroutineScope.launch(IO) {
                archiveListViewModel.fileToShare = convertTextToPdf(
                    context = context,
                    text = archiveListViewModel.processItem?.text.orEmpty(),
                    fileName = fileName.orEmpty()
                )

                shouldSharePdf = true
                isConvertingPdf = false
            }
        } else {
            archiveListViewModel.jobConverting?.cancel()
        }
    }

    LaunchedEffect(isConvertingTxt) {
        if (isConvertingTxt) {
            archiveListViewModel.jobConverting?.cancel()
            archiveListViewModel.jobConverting = coroutineScope.launch(IO) {
                archiveListViewModel.fileToShare = convertTextToTXTFile(
                    context = context,
                    text = archiveListViewModel.processItem?.text.orEmpty(),
                    fileName = fileName.orEmpty()
                )

                shouldShareTxt = true
                isConvertingTxt = false
            }
        } else {
            archiveListViewModel.jobConverting?.cancel()
        }
    }

    LaunchedEffect(shouldSharePdf) {
        if (shouldSharePdf) {
            modalBottomSheetState.hide()
            archiveListViewModel.fileToShare?.let {
                sharePdf(context = context, file = it)
                shouldSharePdf = false
            }
        }
    }

    LaunchedEffect(shouldShareTxt) {
        if (shouldShareTxt) {
            modalBottomSheetState.hide()
            archiveListViewModel.fileToShare?.let {
                shareTXT(context = context, file = it)
                shouldShareTxt = false
            }
        }
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackBarWithPaddingBottom(it, true, 400f)
        }
    ) { innerPadding ->
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = if (isAnyBottomSheetOtherThanUpdate) {
                modalBottomSheetState
            } else {
                modalBottomSheetStateUpdate
            },
            sheetContent = {
                when (selectedSheet) {
                    ArchiveBottomSheetType.ChooseFile -> {
                        ChooseFileContentBottomSheet(
                            descriptionFileFormat = R.string.lbl_allowed_format,
                            descriptionTimeNeed = R.string.lbl_limit_time,
                            onOpenFile = {
                                coroutineScope.launch {
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }

                                // PermissionCheck Duplicate 1
                                val permission = if (isSdkVersion33orHigher()) {
                                    Manifest.permission.READ_MEDIA_AUDIO
                                } else {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                }

                                if (context.hasPermission(permission)) {
                                    launchOpenFile.launch(openAudioSelector())
                                } else if (archiveListViewModel.hasDeniedPermissionPermanently(
                                        permission
                                    )
                                ) {
                                    // needs improvement, just need to save if permission is alreadyRequested
                                    // and everytime check shouldShow

                                    setSelectedSheet(
                                        ArchiveBottomSheetType.FileAccessPermissionDenied
                                    )
                                    coroutineScope.launch {
                                        modalBottomSheetState.hide()
                                        if (!modalBottomSheetState.isVisible) {
                                            modalBottomSheetState.show()
                                        } else {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                } else {
                                    // Asking for permission
                                    chooseAudioPermLauncher.launch(permission)
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.RenameUploading -> {
                        RenameFileContentBottomSheet(
                            fileName.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoardUploadingName.value,
                            renameAction = {
                                fileName = it
                                archiveListViewModel.addFileToUploadingQueue(it, fileUri)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.Update -> {
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

                    ArchiveBottomSheetType.Rename -> {
                        RenameFileBottomSheet(
                            fileName = fileName.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoard.value,
                            reNameAction = { name ->
                                fileName = name
                                archiveListViewModel.updateTitle(
                                    title = name,
                                    id = archiveListViewModel.processItem?.id
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.Detail -> {
                        DetailItemBottomSheet(
                            text = archiveListViewModel.processItem?.title.orEmpty(),
                            copyItemAction = {
                                localClipBoardManager.setText(
                                    AnnotatedString(
                                        archiveListViewModel.processItem?.text.orEmpty()
                                    )
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }

                                showMessage(
                                    snackbarHostState,
                                    coroutineScope,
                                    context.getString(R.string.lbl_text_save_in_clipboard)
                                )
                            },
                            shareItemAction = {
                                setSelectedSheet(ArchiveBottomSheetType.Share)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            },
                            renameItemAction = {
                                setSelectedSheet(ArchiveBottomSheetType.Rename)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            },
                            deleteItemAction = {
                                setSelectedSheet(ArchiveBottomSheetType.DeleteConfirmation)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.Share -> {
                        ShareDetailItemBottomSheet(
                            isConverting = isConvertingPdf || isConvertingTxt,
                            onPdfClick = { isConvertingPdf = true },
                            onTextClick = { isConvertingTxt = true },
                            onOnlyTextClick = {
                                shareText(
                                    context = context,
                                    text = archiveListViewModel.processItem?.text.orEmpty()
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            fileId = archiveListViewModel.processItem?.id?.let { "$it" }
                        )
                    }

                    ArchiveBottomSheetType.DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                when (val file = archiveListViewModel.archiveViewItem) {
                                    is AvanegarTrackingFileView -> {
                                        eventHandler.selectItem(
                                            AvanegarAnalytics.selectDeleteFile(Tracking)
                                        )
                                        archiveListViewModel.removeTrackingFile(file.token)
                                    }

                                    is AvanegarUploadingFileView -> {
                                        eventHandler.selectItem(
                                            AvanegarAnalytics.selectDeleteFile(Uploading)
                                        )
                                        archiveListViewModel.removeUploadingFile(file)
                                    }

                                    is AvanegarProcessedFileView -> {
                                        eventHandler.selectItem(
                                            AvanegarAnalytics.selectDeleteFile(Processed)
                                        )
                                        archiveListViewModel.removeProcessedFile(
                                            archiveListViewModel.processItem?.id
                                        )
                                    }
                                }

                                File(
                                    archiveListViewModel.processItem?.filePath.orEmpty()
                                ).delete()
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            cancelAction = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            fileName = archiveListViewModel.processItem?.title.orEmpty()
                        )
                    }

                    ArchiveBottomSheetType.Delete -> {
                        DeleteBottomSheet(
                            fileName = archiveListViewModel.archiveViewItem?.title.orEmpty(),
                            onDelete = {
                                setSelectedSheet(ArchiveBottomSheetType.DeleteConfirmation)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.AudioAccessPermissionDenied -> {
                        AccessDeniedToOpenMicrophoneBottomSheet(
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

                    ArchiveBottomSheetType.FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(cancelAction = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        }, submitAction = {
                            navigateToAppSettings(activity = context as Activity)
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        })
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ArchiveAppBar(
                        modifier = Modifier.padding(top = 8.dp),
                        onBackClick = { navHostController.navigateUp() },
                        isGrid = isGrid,
                        isListEmpty = archiveFiles.isEmpty(),
                        onUploadClick = {
                            if (isUploadingAllowed) {
                                eventHandler.specialEvent(AvanegarAnalytics.selectUploadFile)
                                snackbarHostState.currentSnackbarData?.dismiss()
                                setSelectedSheet(ArchiveBottomSheetType.ChooseFile)
                                coroutineScope.launch {
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            } else {
                                val hasError = uiViewState is UiError && isThereAnyTrackingOrUploading
                                AvanegarSentry.queueIsFull(hasError)
                                eventHandler.specialEvent(AvanegarAnalytics.uploadNotAllowed(true))
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
                            }
                        },
                        onChangeListTypeClick = {
                            eventHandler.selectItem(AvanegarAnalytics.selectListViewType(!isGrid))
                            archiveListViewModel.saveListType(!isGrid)
                        },
                        onSearchClick = {
                            navHostController.navigate(ScreenRoutes.AvaNegarSearch.route)
                        }
                    )

                    val noNetworkAvailable by remember(networkStatus) {
                        mutableStateOf(networkStatus is NetworkStatus.Unavailable)
                    }

                    val hasVpnConnection by remember(networkStatus) {
                        mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
                    }

                    val isBannerError by remember(uiViewState) {
                        mutableStateOf(uiViewState.let { it is UiError && !it.isSnack })
                    }

                    val shouldShowError by remember(archiveFiles, isThereAnyTrackingOrUploading) {
                        mutableStateOf(archiveFiles.isNotEmpty() && isThereAnyTrackingOrUploading)
                    }

                    ViraBannerWithAnimation(
                        isVisible = (noNetworkAvailable || hasVpnConnection || isBannerError) && shouldShowError,
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

                    ArchiveBody(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        archiveViewList = archiveFiles,
                        failureList = failureList,
                        isNetworkAvailable = !noNetworkAvailable,
                        isUploading = uploadingFileState == UploadingFileStatus.Uploading,
                        isGrid = isGrid,
                        uploadingId = uploadingId,
                        listState = listState,
                        brush = if (isGrid) gridBrush() else columnBrush(),
                        onTryAgainCLick = { archiveListViewModel.startUploading(it) },
                        onMenuClick = { item ->
                            when (item) {
                                is AvanegarProcessedFileView -> {
                                    setSelectedSheet(ArchiveBottomSheetType.Detail)
                                    coroutineScope.launch {
                                        if (!modalBottomSheetState.isVisible) {
                                            modalBottomSheetState.show()
                                        } else {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                    archiveListViewModel.archiveViewItem = item
                                    archiveListViewModel.processItem = item
                                    fileName = item.title
                                }

                                else -> {
                                    setSelectedSheet(ArchiveBottomSheetType.Delete)
                                    coroutineScope.launch {
                                        if (!modalBottomSheetState.isVisible) {
                                            modalBottomSheetState.show()
                                        } else {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                    archiveListViewModel.archiveViewItem = item
                                    fileName = item.title
                                }
                            }
                        },
                        onItemClick = { id, title ->
                            navHostController.navigate(
                                ScreenRoutes.AvaNegarArchiveDetail.createRoute(id, title)
                            )
                        }
                    )
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .align(BottomStart)
                        .padding(
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
                                if (isUploadingAllowed) {
                                    eventHandler.specialEvent(
                                        AvanegarAnalytics.selectRecordAudio(
                                            if (context.hasRecordAudioPermission()) "1" else "0"
                                        )
                                    )

                                    snackbarHostState.currentSnackbarData?.dismiss()

                                    if (context.packageManager.hasSystemFeature(
                                            PackageManager.FEATURE_MICROPHONE
                                        )
                                    ) {
                                        // PermissionCheck Duplicate 2
                                        if (context.hasRecordAudioPermission()) {
                                            gotoRecordAudioScreen(navHostController)
                                        } else {
                                            // needs improvement, just need to save if permission is alreadyRequested
                                            // and everytime check shouldShow
                                            if (archiveListViewModel.hasDeniedPermissionPermanently(
                                                    Manifest.permission.RECORD_AUDIO
                                                )
                                            ) {
                                                setSelectedSheet(
                                                    ArchiveBottomSheetType.AudioAccessPermissionDenied
                                                )
                                                coroutineScope.launch {
                                                    if (!modalBottomSheetState.isVisible) {
                                                        modalBottomSheetState.show()
                                                    } else {
                                                        modalBottomSheetState.hide()
                                                    }
                                                }
                                            } else {
                                                recordAudioPermLauncher.launch(
                                                    Manifest.permission.RECORD_AUDIO
                                                )
                                            }
                                        }
                                    } else {
                                        showMessage(
                                            snackbarHostState,
                                            coroutineScope,
                                            context.getString(
                                                R.string.msg_no_microphone_found_on_phone
                                            )
                                        )
                                    }
                                } else {
                                    val hasError = uiViewState is UiError && isThereAnyTrackingOrUploading
                                    AvanegarSentry.queueIsFull(hasError)
                                    eventHandler.specialEvent(
                                        AvanegarAnalytics.uploadNotAllowed(
                                            false
                                        )
                                    )
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
                                }
                            }
                        }
                    ) {
                        ViraIcon(
                            drawable = R.drawable.ic_mic,
                            contentDescription = stringResource(id = R.string.desc_record)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveAppBar(
    isGrid: Boolean,
    isListEmpty: Boolean,
    onChangeListTypeClick: () -> Unit,
    onUploadClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = CenterVertically,
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
            text = stringResource(id = R.string.lbl_ava_negar),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.size(8.dp))

        IconButton(
            onClick = {
                safeClick {
                    onUploadClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_upload,
                contentDescription = stringResource(R.string.desc_upload),
                modifier = Modifier.padding(12.dp)
            )
        }

        if (!isListEmpty) {
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
                        id = if (isGrid) {
                            R.string.desc_grid
                        } else {
                            R.string.desc_column
                        }
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }

            IconButton(
                onClick = {
                    safeClick {
                        onSearchClick()
                    }
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_search,
                    modifier = Modifier.padding(12.dp),
                    contentDescription = stringResource(id = R.string.desc_search)
                )
            }
        }
    }
}

@Composable
private fun ArchiveBody(
    archiveViewList: List<ArchiveView>,
    failureList: List<AvanegarUploadingFileView>,
    isNetworkAvailable: Boolean,
    isUploading: Boolean,
    isGrid: Boolean,
    brush: Brush,
    uploadingId: String,
    listState: LazyListState,
    onTryAgainCLick: (AvanegarUploadingFileView) -> Unit,
    onMenuClick: (ArchiveView) -> Unit,
    onItemClick: (id: Int, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (archiveViewList.isEmpty()) {
        ArchiveEmptyBody(
            modifier = modifier
        )
    } else {
        ArchiveList(
            list = archiveViewList,
            failureList = failureList,
            isNetworkAvailable = isNetworkAvailable,
            isUploading = isUploading,
            isGrid = isGrid,
            brush = brush,
            uploadingId = uploadingId,
            listState = listState,
            onTryAgainCLick = { onTryAgainCLick(it) },
            onMenuClick = { onMenuClick(it) },
            onItemClick = { id, title -> onItemClick(id, title) }
        )
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
                text = stringResource(id = R.string.lbl_dont_have_file),
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
private fun ArchiveList(
    list: List<ArchiveView>,
    failureList: List<AvanegarUploadingFileView>,
    isNetworkAvailable: Boolean,
    isUploading: Boolean,
    isGrid: Boolean,
    brush: Brush,
    uploadingId: String,
    listState: LazyListState,
    onTryAgainCLick: (AvanegarUploadingFileView) -> Unit,
    onMenuClick: (ArchiveView) -> Unit,
    onItemClick: (id: Int, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isGrid) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = list,
                key = { item ->
                    when (item) {
                        is AvanegarProcessedFileView -> item.id

                        is AvanegarTrackingFileView -> item.token

                        is AvanegarUploadingFileView -> item.id
                        else -> {}
                    }
                }
            ) {
                when (it) {
                    is AvanegarProcessedFileView -> {
                        ArchiveProcessedFileElementGrid(
                            archiveViewProcessed = it,
                            onItemClick = { id, title ->
                                onItemClick(id, title)
                            },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            }
                        )
                    }

                    is AvanegarTrackingFileView -> {
                        ArchiveTrackingFileElementGrid(
                            archiveTrackingView = it,
                            brush = brush,
                            onItemClick = {},
                            onMenuClick = { item -> onMenuClick(item) },
                            estimateTime = { it.computeFileEstimateProcess() }
                        )
                    }

                    is AvanegarUploadingFileView -> {
                        ArchiveUploadingFileElementGrid(
                            archiveUploadingFileView = it,
                            isUploading = isUploading,
                            isNetworkAvailable = isNetworkAvailable,
                            uploadingId = uploadingId,
                            isFailure = failureList.contains(it),
                            onTryAgainClick = { value -> onTryAgainCLick(value) },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            },
                            onItemClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(list) {
                when (it) {
                    is AvanegarProcessedFileView -> {
                        ArchiveProcessedFileElementColumn(
                            archiveViewProcessed = it,
                            onItemClick = { id, title ->
                                onItemClick(id, title)
                            },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            }
                        )
                    }

                    is AvanegarTrackingFileView -> {
                        ArchiveTrackingFileElementsColumn(
                            archiveTrackingView = it,
                            onItemClick = {
                            },
                            brush = brush,
                            onMenuClick = { item -> onMenuClick(item) },
                            estimateTime = { it.computeFileEstimateProcess() }
                        )
                    }

                    is AvanegarUploadingFileView -> {
                        ArchiveUploadingFileElementColumn(
                            archiveUploadingFileView = it,
                            isUploading = isUploading,
                            isNetworkAvailable = isNetworkAvailable,
                            uploadingId = uploadingId,
                            isFailure = failureList.contains(it),
                            onTryAgainClick = { value -> onTryAgainCLick(value) },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            },
                            onItemClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun gridBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0.01f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = TRACKING_FILE_ANIMATION_DURATION_Grid,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
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

@Composable
private fun columnBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0.01f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = TRACKING_FILE_ANIMATION_DURATION_Column,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
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

private fun gotoRecordAudioScreen(navHostController: NavHostController) {
    navHostController.navigate(ScreenRoutes.AvaNegarVoiceRecording.route)
}

@ViraDarkPreview
@Composable
private fun ArchiveBodyErrorPreview() {
    ViraPreview {
        ArchiveAppBar(
            modifier = Modifier,
            onBackClick = {},
            isGrid = true,
            isListEmpty = false,
            onUploadClick = {},
            onChangeListTypeClick = {},
            onSearchClick = {}
        )
    }
}

@ViraDarkPreview
@Composable
private fun ArchiveEmptyBodyPreview() {
    ViraPreview {
        ArchiveEmptyBody()
    }
}