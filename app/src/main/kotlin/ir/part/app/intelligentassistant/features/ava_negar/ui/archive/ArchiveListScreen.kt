package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.SnackBarWithPaddingBottom
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveProcessedFileElementColumn
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveProcessedFileElementGrid
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveTrackingFileElementGrid
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveTrackingFileElementsColumn
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveUploadingFileElementColumn
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveUploadingFileElementGrid
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.ArchiveView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.UploadingFileStatus
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.RecordFileResult
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.RecordFileResult.Companion.FILE_NAME
import ir.part.app.intelligentassistant.features.ava_negar.ui.update.ForceUpdateScreen
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.file.convertTextToTXTFile
import ir.part.app.intelligentassistant.utils.common.file.filename
import ir.part.app.intelligentassistant.utils.ui.Constants
import ir.part.app.intelligentassistant.utils.ui.UiError
import ir.part.app.intelligentassistant.utils.ui.UiIdle
import ir.part.app.intelligentassistant.utils.ui.isPermissionDeniedPermanently
import ir.part.app.intelligentassistant.utils.ui.navigateToAppSettings
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.sharePdf
import ir.part.app.intelligentassistant.utils.ui.shareTXT
import ir.part.app.intelligentassistant.utils.ui.shareText
import ir.part.app.intelligentassistant.utils.ui.showMessage
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Bottom_Sheet
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red_800
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import kotlinx.coroutines.launch
import java.io.File
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun AvaNegarArchiveListScreen(
    navHostController: NavHostController,
    archiveViewModel: ArchiveViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isFabExpanded by rememberSaveable { mutableStateOf(false) }

    val localClipBoardManager = LocalClipboardManager.current

    var isConverting by rememberSaveable { mutableStateOf(false) }

    val fileName = rememberSaveable { mutableStateOf<String?>("") }

    val fileUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    var isGrid by rememberSaveable { mutableStateOf(true) }

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
        skipHalfExpanded = true
    )

    val modalBottomSheetStateUpdate = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { false }
    )

    val uploadingFileState by archiveViewModel.isUploading.collectAsStateWithLifecycle(
        UploadingFileStatus.Idle
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    val uiViewState by archiveViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/*"
    val mimetypes = arrayOf("audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

    val updateIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CAFEBAZAAR_LINK))

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            setSelectedSheet(ArchiveBottomSheetType.RenameUploading)
            coroutineScope.launch {
                if (!modalBottomSheetState.isVisible) {
                    modalBottomSheetState.show()
                } else {
                    modalBottomSheetState.hide()
                }
            }
            try {
                fileName.value =
                    it.data?.data?.filename(context).orEmpty()
                fileUri.value = it.data?.data
            } catch (_: Exception) {

            }
        }
    }
    val chooseAudioPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchOpenFile.launch(intent)
        } else {
            val permission = if (Build.VERSION.SDK_INT >= 33) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            archiveViewModel.putDeniedPermissionToSharedPref(
                permission = permission,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = permission
                )
            )

            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(AIResource.string.lbl_need_to_access_file_permission)
            )
        }
    }

    val recordAudioPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            gotoRecordAudioScreen(navHostController)
        } else {
            archiveViewModel.putDeniedPermissionToSharedPref(
                permission = Manifest.permission.RECORD_AUDIO,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = Manifest.permission.RECORD_AUDIO
                )
            )

            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(AIResource.string.lbl_need_to_access_to_record_audio_permission)
            )
        }
    }

    navHostController.currentBackStackEntry
        ?.savedStateHandle?.remove<RecordFileResult>(FILE_NAME)?.let {
            archiveViewModel.addFileToUploadingQueue(it.title, Uri.fromFile(File(it.filepath)))
        }

    BackHandler(modalBottomSheetStateUpdate.isVisible) {
        //we want to disable back
    }

    BackHandler(isFabExpanded || (modalBottomSheetState.isVisible && !modalBottomSheetStateUpdate.isVisible)) {
        if (isFabExpanded)
            isFabExpanded = false
        if (modalBottomSheetState.isVisible) {
            coroutineScope.launch {
                modalBottomSheetState.hide()
            }
        }
    }

    val shouldShowKeyBoard = rememberSaveable { mutableStateOf(false) }
    val shouldShowKeyBoardUploadingName = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(modalBottomSheetState.targetValue) {

        if (modalBottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
            if (selectedSheet.name == ArchiveBottomSheetType.Rename.name)
                shouldShowKeyBoard.value = true

            if (selectedSheet.name == ArchiveBottomSheetType.RenameUploading.name)
                shouldShowKeyBoardUploadingName.value = true

        } else {
            shouldShowKeyBoard.value = false
            shouldShowKeyBoardUploadingName.value = false
        }
    }

    LaunchedEffect(archiveViewModel.aiEvent.value) {
        if (archiveViewModel.aiEvent.value == IntelligentAssistantEvent.TokenExpired) {
            setSelectedSheet(ArchiveBottomSheetType.Update)
            modalBottomSheetStateUpdate.show()
        }
    }

    Scaffold(
        backgroundColor = if (archiveViewModel.allArchiveFiles.value.isEmpty())
            MaterialTheme.colors.background
        else
            Color.Transparent,
        modifier = if (archiveViewModel.allArchiveFiles.value.isNotEmpty())
            Modifier.paint(
                painter = painterResource(id = R.drawable.bg_pattern),
                contentScale = ContentScale.Crop
            )
        else
            Modifier,
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackBarWithPaddingBottom(it, isFabExpanded)
        },
    ) { innerPadding ->
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = if (isAnyBottomSheetOtherThanUpdate) modalBottomSheetState
            else modalBottomSheetStateUpdate,
            sheetContent = {
                when (selectedSheet) {
                    ArchiveBottomSheetType.ChooseFile -> {
                        ChooseFileBottomSheetContent(onOpenFile = {
                            coroutineScope.launch {
                                if (!modalBottomSheetState.isVisible) {
                                    modalBottomSheetState.show()
                                } else modalBottomSheetState.hide()
                            }

                            // PermissionCheck Duplicate 1
                            val permission = if (Build.VERSION.SDK_INT >= 33) {
                                Manifest.permission.READ_MEDIA_AUDIO
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }

                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                launchOpenFile.launch(intent)
                            } else if (archiveViewModel.hasDeniedPermissionPermanently(permission)) {
                                // needs improvement, just need to save if permission is alreadyRequested
                                // and everytime check shouldShow
                                navigateToAppSettings(activity = context as Activity)

                                showMessage(
                                    snackbarHostState,
                                    coroutineScope,
                                    context.getString(
                                        AIResource.string.msg_access_file_permission_manually
                                    )
                                )
                            } else {
                                // Asking for permission
                                chooseAudioPermLauncher.launch(permission)
                            }
                        })
                    }

                    ArchiveBottomSheetType.RenameUploading -> {
                        RenameFileBottomSheetContent(
                            fileName.value.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoardUploadingName.value,
                            renameAction = {
                                fileName.value = it
                                archiveViewModel.addFileToUploadingQueue(it, fileUri.value)
                                isFabExpanded = false
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.Update -> {
                        ForceUpdateScreen(
                            onUpdateClick = {
                                ContextCompat.startActivity(context, updateIntent, null)
                            }
                        )
                    }

                    ArchiveBottomSheetType.Rename -> {
                        RenameFile(
                            fileName = fileName.value.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoard.value,
                            onValueChange = { fileName.value = it },
                            reNameAction = {
                                archiveViewModel.updateTitle(
                                    title = fileName.value.orEmpty(),
                                    id = archiveViewModel.processItem?.id
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.Detail -> {
                        BottomSheetDetailItem(
                            text = archiveViewModel.processItem?.title.orEmpty(),
                            copyItemAction = {
                                localClipBoardManager.setText(
                                    AnnotatedString(
                                        archiveViewModel.processItem?.text.orEmpty()
                                    )
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }

                                showMessage(
                                    snackbarHostState,
                                    coroutineScope,
                                    context.getString(AIResource.string.lbl_text_save_in_clipboard)
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
                            },
                        )
                    }

                    ArchiveBottomSheetType.Share -> {
                        BottomSheetShareDetailItem(
                            isConverting = isConverting,
                            onPdfClick = {
                                coroutineScope.launch {
                                    isConverting = true

                                    val pdfFile = convertTextToPdf(
                                        fileName.value.orEmpty(),
                                        text = archiveViewModel.processItem?.text.orEmpty(),
                                        context
                                    )
                                    isConverting = false

                                    modalBottomSheetState.hide()

                                    pdfFile?.let {
                                        sharePdf(context = context, file = it)
                                    }
                                }
                            },
                            onTextClick = {
                                coroutineScope.launch {
                                    isConverting = true

                                    val file = convertTextToTXTFile(
                                        context = context,
                                        text = archiveViewModel.processItem?.text.orEmpty(),
                                        fileName = fileName.value.orEmpty()
                                    )

                                    isConverting = false
                                    modalBottomSheetState.hide()

                                    file?.let {
                                        shareTXT(context = context, file = it)
                                    }
                                }
                            },
                            onOnlyTextClick = {
                                shareText(
                                    context = context,
                                    text = archiveViewModel.processItem?.text.orEmpty()
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    ArchiveBottomSheetType.DeleteConfirmation -> {
                        DeleteFileItemConfirmationBottomSheet(
                            deleteAction = {

                                when (val file = archiveViewModel.archiveViewItem) {
                                    is AvanegarTrackingFileView ->
                                        archiveViewModel.removeTrackingFile(file.token)

                                    is AvanegarUploadingFileView ->
                                        archiveViewModel.removeUploadingFile(file.id)

                                    is AvanegarProcessedFileView ->
                                        archiveViewModel.removeProcessedFile(archiveViewModel.processItem?.id)

                                }

                                File(
                                    archiveViewModel.processItem?.filePath.orEmpty()
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
                            fileName = archiveViewModel.processItem?.title.orEmpty()
                        )
                    }

                    ArchiveBottomSheetType.Delete -> {
                        DeleteBottomSheet(
                            fileName = archiveViewModel.archiveViewItem?.title.orEmpty(),
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

                }
            }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                Column(modifier = Modifier.fillMaxSize()) {
                    ArchiveAppBar(modifier = Modifier
                        .padding(top = 8.dp),
                        onBackClick = { navHostController.navigateUp() },
                        isGrid = isGrid,
                        onChangeListTypeClick = { isGrid = !isGrid },
                        onSearchClick = {
                            navHostController.navigate(
                                ScreenRoutes.AvaNegarSearch.route
                            )
                        })

                    if (
                        (!archiveViewModel.isNetworkAvailable.value && archiveViewModel.allArchiveFiles.value.isNotEmpty()) ||
                        uiViewState is UiError
                    )
                        ErrorBanner(
                            errorMessage = if (uiViewState is UiError) (uiViewState as UiError).message
                            else stringResource(id = R.string.msg_internet_disconnected)
                        )

                    ArchiveBody(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        archiveViewList = archiveViewModel.allArchiveFiles.value,
                        isNetworkAvailable = archiveViewModel.isNetworkAvailable.value,
                        isUploading = uploadingFileState == UploadingFileStatus.Uploading,
                        isErrorState = uiViewState is UiError,
                        isGrid = isGrid,
                        onTryAgainCLick = { archiveViewModel.startUploading(it) },
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
                                    archiveViewModel.archiveViewItem = item
                                    archiveViewModel.processItem = item
                                    fileName.value = item.title
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
                                    archiveViewModel.archiveViewItem = item
                                    fileName.value = item.title
                                }
                            }

                        },
                        onItemClick = {
                            navHostController.navigate(
                                ScreenRoutes.AvaNegarArchiveDetail.route.plus(
                                    "/$it"
                                )
                            )

                        }
                    )
                }


                if (isFabExpanded) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) { isFabExpanded = false },
                        content = {}
                    )
                }
                Fabs(isFabExpanded = isFabExpanded,
                    modifier = Modifier.align(Alignment.BottomStart),
                    onMainFabClick = {
                        isFabExpanded = !isFabExpanded
                    },
                    selectFile = {
                        isFabExpanded = false
                        snackbarHostState.currentSnackbarData?.dismiss()
                        setSelectedSheet(ArchiveBottomSheetType.ChooseFile)
                        coroutineScope.launch {
                            if (!modalBottomSheetState.isVisible) {
                                modalBottomSheetState.show()
                            } else {
                                modalBottomSheetState.hide()
                            }
                        }
                    },
                    openRecordingScreen = {
                        isFabExpanded = false
                        snackbarHostState.currentSnackbarData?.dismiss()

                        // PermissionCheck Duplicate 2
                        if (
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            gotoRecordAudioScreen(navHostController)
                        } else {
                            // needs improvement, just need to save if permission is alreadyRequested
                            // and everytime check shouldShow
                            if (archiveViewModel.hasDeniedPermissionPermanently(Manifest.permission.RECORD_AUDIO)) {
                                navigateToAppSettings(activity = context as Activity)

                                showMessage(
                                    snackbarHostState,
                                    coroutineScope,
                                    context.getString(
                                        AIResource.string.msg_record_audio_permission_manually
                                    )
                                )
                            } else {
                                recordAudioPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }

                    }
                )
            }
        }
    }
}

@Composable
private fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    isGrid: Boolean,
    onChangeListTypeClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = AIResource.drawable.ic_arrow_forward),
                contentDescription = stringResource(id = AIResource.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = AIResource.string.lbl_ava_negar),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.size(8.dp))

        IconButton(onClick = onChangeListTypeClick) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(
                    id = if (isGrid) AIResource.drawable.ic_list_column
                    else AIResource.drawable.ic_list_grid
                ),
                contentDescription = stringResource(
                    id = if (isGrid) AIResource.string.desc_grid
                    else AIResource.string.desc_column
                )
            )
        }

        IconButton(onClick = onSearchClick) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = AIResource.drawable.ic_search),
                contentDescription = stringResource(id = AIResource.string.desc_search)
            )
        }
    }
}

@Composable
private fun ArchiveBody(
    modifier: Modifier,
    archiveViewList: List<ArchiveView>,
    isNetworkAvailable: Boolean,
    isErrorState: Boolean,
    isUploading: Boolean,
    isGrid: Boolean,
    onTryAgainCLick: (AvanegarUploadingFileView) -> Unit,
    onMenuClick: (ArchiveView) -> Unit,
    onItemClick: (Int) -> Unit
) {
    if (archiveViewList.isEmpty()) {
        ArchiveEmptyBody(
            modifier = modifier
        )
    } else {
        ArchiveList(
            list = archiveViewList,
            isNetworkAvailable = isNetworkAvailable,
            isUploading = isUploading,
            isErrorState = isErrorState,
            isGrid = isGrid,
            onTryAgainCLick = { onTryAgainCLick(it) },
            onMenuClick = { onMenuClick(it) },
            onItemClick = { onItemClick(it) }
        )
    }
}

@Composable
fun ErrorBanner(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color_Red_800)
            .padding(8.dp)
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_failure_network),
            contentDescription = null,
            tint = Color_Red
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.body2,
            color = Color_Red
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
            verticalArrangement = Arrangement.Bottom,
        ) {
            Image(
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = AIResource.drawable.img_main_page),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = AIResource.string.lbl_dont_have_file),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = AIResource.string.lbl_make_your_first_file),
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

            Image(
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = AIResource.drawable.ic_arrow),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.size(60.dp))
    }
}

@Composable
private fun ArchiveList(
    list: List<ArchiveView>,
    modifier: Modifier = Modifier,
    isNetworkAvailable: Boolean,
    isUploading: Boolean,
    isErrorState: Boolean,
    isGrid: Boolean,
    onTryAgainCLick: (AvanegarUploadingFileView) -> Unit,
    onMenuClick: (ArchiveView) -> Unit,
    onItemClick: (Int) -> Unit
) {

    if (isGrid)
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                }) {

                when (it) {
                    is AvanegarProcessedFileView -> {
                        ArchiveProcessedFileElementGrid(
                            archiveViewProcessed = it,
                            onItemClick = { id ->
                                onItemClick(id)
                            },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            }
                        )
                    }

                    is AvanegarTrackingFileView -> {
                        ArchiveTrackingFileElementGrid(
                            archiveTrackingView = it,
                            isNetworkAvailable = isNetworkAvailable,
                            onItemClick = {},
                            onMenuClick = { item -> onMenuClick(item) }
                        )
                    }

                    is AvanegarUploadingFileView -> {
                        ArchiveUploadingFileElementGrid(
                            archiveUploadingFileView = it,
                            isUploading = isUploading,
                            isNetworkAvailable = isNetworkAvailable,
                            isErrorState = isErrorState,
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
    else
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(list) {
                when (it) {
                    is AvanegarProcessedFileView -> {
                        ArchiveProcessedFileElementColumn(
                            archiveViewProcessed = it,
                            onItemClick = { id ->
                                onItemClick(id)
                            },
                            onMenuClick = { item ->
                                onMenuClick(item)
                            }
                        )
                    }

                    is AvanegarTrackingFileView -> {
                        ArchiveTrackingFileElementsColumn(
                            archiveTrackingView = it,
                            isNetworkAvailable = isNetworkAvailable,
                            onItemClick = {

                            },
                            onMenuClick = { item -> onMenuClick(item) }
                        )
                    }

                    is AvanegarUploadingFileView -> {
                        ArchiveUploadingFileElementColumn(
                            archiveUploadingFileView = it,
                            isUploading = isUploading,
                            isNetworkAvailable = isNetworkAvailable,
                            isErrorState = isErrorState,
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

@Composable
private fun Fabs(
    modifier: Modifier = Modifier,
    isFabExpanded: Boolean,
    onMainFabClick: () -> Unit,
    selectFile: () -> Unit,
    openRecordingScreen: () -> Unit
) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = modifier.padding(
            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
        )
    ) {
        Column {

            AnimatedVisibility(visible = isFabExpanded) {

                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(bottom = 18.dp),
                    onClick = selectFile
                ) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_upload),
                        contentDescription = stringResource(id = AIResource.string.desc_upload)
                    )
                }
            }

            FloatingActionButton(
                backgroundColor = if (isFabExpanded) Color_White else MaterialTheme.colors.primary,
                modifier = Modifier
                    .clip(CircleShape),
                onClick = onMainFabClick
            ) {
                Icon(
                    tint = if (isFabExpanded) MaterialTheme.colors.primary else Color_White,
                    painter = painterResource(id = if (isFabExpanded) AIResource.drawable.ic_close else AIResource.drawable.ic_add),
                    contentDescription = stringResource(id = AIResource.string.desc_menu_upload_and_record)
                )
            }
        }
        AnimatedVisibility(visible = isFabExpanded) {

            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp, start = 8.dp),
                onClick = {
                    openRecordingScreen()
                }) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.ic_mic),
                    contentDescription = stringResource(id = AIResource.string.desc_record)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArchiveBodyErrorPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveAppBar(
                modifier = Modifier,
                onBackClick = {},
                isGrid = true,
                onChangeListTypeClick = {},
                onSearchClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArchiveEmptyBodyPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveEmptyBody()
        }
    }
}

private fun gotoRecordAudioScreen(navHostController: NavHostController) {
    navHostController.navigate(ScreenRoutes.AvaNegarVoiceRecording.route)
}