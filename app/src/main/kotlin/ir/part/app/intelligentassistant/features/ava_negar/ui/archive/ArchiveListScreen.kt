package ir.part.app.intelligentassistant.features.ava_negar.ui.archive

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
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
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenMicrophoneBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.ChooseFileContentBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.DetailItemBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.RenameFileContentBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.ShareDetailItemBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.details.TIME_INTERVAL
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
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.sharePdf
import ir.part.app.intelligentassistant.utils.ui.shareTXT
import ir.part.app.intelligentassistant.utils.ui.shareText
import ir.part.app.intelligentassistant.utils.ui.showMessage
import ir.part.app.intelligentassistant.utils.ui.theme.BLue_a200_Opacity_40
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Bottom_Sheet
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red_800
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.ui.widgets.ViraImage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val TRACKING_FILE_ANIMATION_DURATION_Column = 1300
const val TRACKING_FILE_ANIMATION_DURATION_Grid = 1500

@Composable
fun AvaNegarArchiveListScreen(
    navHostController: NavHostController,
    archiveListViewModel: ArchiveListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isFabExpanded by rememberSaveable { mutableStateOf(false) }

    val localClipBoardManager = LocalClipboardManager.current

    var isConvertingPdf by rememberSaveable { mutableStateOf(false) }
    var isConvertingTxt by rememberSaveable { mutableStateOf(false) }
    var shouldSharePdf by rememberSaveable { mutableStateOf(false) }
    var shouldShareTxt by rememberSaveable { mutableStateOf(false) }

    val fileName = rememberSaveable { mutableStateOf<String?>("") }

    val fileUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    val isGrid by archiveListViewModel.isGrid.collectAsStateWithLifecycle()

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
    val isThereAnyTrackingOrUploading by archiveListViewModel.isThereAnyTrackingOrUploading.collectAsStateWithLifecycle()

    val isNetworkAvailable by archiveListViewModel.isNetworkAvailable.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    val uiViewState by archiveListViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

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

    var backPressedInterval: Long = 0

    navHostController.currentBackStackEntry
        ?.savedStateHandle?.remove<RecordFileResult>(FILE_NAME)?.let {
            archiveListViewModel.addFileToUploadingQueue(it.title, Uri.fromFile(File(it.filepath)))
        }

    BackHandler(modalBottomSheetStateUpdate.isVisible) {
        //we want to disable back
    }

    BackHandler(isFabExpanded || (modalBottomSheetState.isVisible && !modalBottomSheetStateUpdate.isVisible)) {
        if (isFabExpanded)
            isFabExpanded = false
        if (modalBottomSheetState.isVisible) {
            coroutineScope.launch {
                if (modalBottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
                    coroutineScope.launch(IO) {
                        if (!isConvertingPdf && !isConvertingTxt)
                            modalBottomSheetState.hide()
                        else {
                            if (backPressedInterval + TIME_INTERVAL < System.currentTimeMillis()) {
                                withContext(Main) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.msg_back_again_to_cancel_converting),
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

    LaunchedEffect(modalBottomSheetState.currentValue) {

        if (modalBottomSheetState.isVisible) {
            if (selectedSheet.name == ArchiveBottomSheetType.Rename.name)
                shouldShowKeyBoard.value = true

            if (selectedSheet.name == ArchiveBottomSheetType.RenameUploading.name)
                shouldShowKeyBoardUploadingName.value = true

        } else {
            shouldShowKeyBoard.value = false
            shouldShowKeyBoardUploadingName.value = false
        }
    }

    LaunchedEffect(archiveListViewModel.aiEvent.value) {
        if (archiveListViewModel.aiEvent.value == IntelligentAssistantEvent.TokenExpired) {
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
                    fileName = fileName.value.orEmpty()
                )

                shouldSharePdf = true
                isConvertingPdf = false
            }

        } else archiveListViewModel.jobConverting?.cancel()
    }

    LaunchedEffect(isConvertingTxt) {
        if (isConvertingTxt) {

            archiveListViewModel.jobConverting?.cancel()
            archiveListViewModel.jobConverting = coroutineScope.launch(IO) {
                archiveListViewModel.fileToShare = convertTextToTXTFile(
                    context = context,
                    text = archiveListViewModel.processItem?.text.orEmpty(),
                    fileName = fileName.value.orEmpty()
                )

                shouldShareTxt = true
                isConvertingTxt = false

            }

        } else archiveListViewModel.jobConverting?.cancel()
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
            SnackBarWithPaddingBottom(it, isFabExpanded, 600f)
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
                        ChooseFileContentBottomSheet(onOpenFile = {
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
                            } else if (archiveListViewModel.hasDeniedPermissionPermanently(
                                    permission
                                )
                            ) {
                                // needs improvement, just need to save if permission is alreadyRequested
                                // and everytime check shouldShow

                                setSelectedSheet(ArchiveBottomSheetType.FileAccessPermissionDenied)
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
                        })
                    }

                    ArchiveBottomSheetType.RenameUploading -> {
                        RenameFileContentBottomSheet(
                            fileName.value.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoardUploadingName.value,
                            renameAction = {
                                fileName.value = it
                                archiveListViewModel.addFileToUploadingQueue(it, fileUri.value)
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
                        RenameFileBottomSheet(
                            fileName = fileName.value.orEmpty(),
                            shouldShowKeyBoard = shouldShowKeyBoard.value,
                            onValueChange = { fileName.value = it },
                            reNameAction = {
                                fileName.value?.let {
                                    archiveListViewModel.updateTitle(
                                        title = it,
                                        id = archiveListViewModel.processItem?.id
                                    )
                                }
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
                            },
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
                            }
                        )
                    }

                    ArchiveBottomSheetType.DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {

                                when (val file = archiveListViewModel.archiveViewItem) {
                                    is AvanegarTrackingFileView ->
                                        archiveListViewModel.removeTrackingFile(file.token)

                                    is AvanegarUploadingFileView ->
                                        archiveListViewModel.removeUploadingFile(file.id)

                                    is AvanegarProcessedFileView ->
                                        archiveListViewModel.removeProcessedFile(
                                            archiveListViewModel.processItem?.id
                                        )

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
                            })
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
                            })
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
                    ArchiveAppBar(modifier = Modifier
                        .padding(top = 8.dp),
                        onBackClick = { navHostController.navigateUp() },
                        isGrid = isGrid,
                        onChangeListTypeClick = {
                            archiveListViewModel.saveListType(
                                !isGrid
                            )
                        },
                        onSearchClick = {

                            navHostController.navigate(
                                ScreenRoutes.AvaNegarSearch.route
                            )
                        })

                    if (
                        (!isNetworkAvailable &&
                                archiveFiles.isNotEmpty() &&
                                isThereAnyTrackingOrUploading) ||
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
                        archiveViewList = archiveFiles,
                        isNetworkAvailable = isNetworkAvailable,
                        isUploading = uploadingFileState == UploadingFileStatus.Uploading,
                        isErrorState = uiViewState is UiError,
                        isGrid = isGrid,
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
                                    archiveListViewModel.archiveViewItem = item
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

                        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
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
                                if (archiveListViewModel.hasDeniedPermissionPermanently(Manifest.permission.RECORD_AUDIO)) {
                                    setSelectedSheet(ArchiveBottomSheetType.AudioAccessPermissionDenied)
                                    coroutineScope.launch {
                                        if (!modalBottomSheetState.isVisible) {
                                            modalBottomSheetState.show()
                                        } else {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                } else {
                                    recordAudioPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
        IconButton(onClick = {
            safeClick {
                onBackClick()
            }
        }) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = R.drawable.ic_arrow_forward),
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

        IconButton(onClick = {
            safeClick {
                onChangeListTypeClick()
            }
        }) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(
                    id = if (isGrid) R.drawable.ic_list_column
                    else R.drawable.ic_list_grid
                ),
                contentDescription = stringResource(
                    id = if (isGrid) R.string.desc_grid
                    else R.string.desc_column
                )
            )
        }

        IconButton(onClick = {
            safeClick {
                onSearchClick()
            }
        }) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.desc_search)
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
    brush: Brush,
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
            brush = brush,
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
    modifier: Modifier = Modifier,
    isNetworkAvailable: Boolean,
    isUploading: Boolean,
    isErrorState: Boolean,
    isGrid: Boolean,
    brush: Brush,
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
                            brush = brush,
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
                            brush = brush,
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

            AnimatedVisibility(
                visible = isFabExpanded,
                modifier = Modifier.clip(CircleShape)
            ) {

                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(bottom = 18.dp),
                    onClick = {
                        safeClick {
                            selectFile()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload),
                        contentDescription = stringResource(id = R.string.desc_upload)
                    )
                }
            }

            FloatingActionButton(
                backgroundColor = if (isFabExpanded) Color_White else MaterialTheme.colors.primary,
                modifier = Modifier.clip(CircleShape),
                onClick = {
                    safeClick {
                        onMainFabClick()
                    }
                }
            ) {
                Icon(
                    tint = if (isFabExpanded) MaterialTheme.colors.primary else Color_White,
                    painter = painterResource(id = if (isFabExpanded) R.drawable.ic_close else R.drawable.ic_add),
                    contentDescription = stringResource(id = R.string.desc_menu_upload_and_record)
                )
            }
        }
        AnimatedVisibility(
            visible = isFabExpanded,
            modifier = Modifier.clip(CircleShape)
        ) {

            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp, start = 8.dp),
                onClick = {
                    safeClick {
                        openRecordingScreen()
                    }
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = stringResource(id = R.string.desc_record)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070707)
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

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun ArchiveEmptyBodyPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveEmptyBody()
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

private fun gotoRecordAudioScreen(navHostController: NavHostController) {
    navHostController.navigate(ScreenRoutes.AvaNegarVoiceRecording.route)
}

