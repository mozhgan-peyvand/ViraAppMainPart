package ir.part.app.intelligentassistant.ui.screen.archive

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.navigation.ScreensRouter
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarUploadingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.BottomSheetDetailItem
import ir.part.app.intelligentassistant.ui.screen.archive.entity.BottomSheetShareDetailItem
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ChooseFileBottomSheetContent
import ir.part.app.intelligentassistant.ui.screen.archive.entity.DeleteFileItemBottomSheet
import ir.part.app.intelligentassistant.ui.screen.archive.entity.RenameFile
import ir.part.app.intelligentassistant.ui.screen.archive.entity.RenameFileBottomSheetContent
import ir.part.app.intelligentassistant.ui.screen.update.ForceUpdateScreen
import ir.part.app.intelligentassistant.ui.theme.Red
import ir.part.app.intelligentassistant.ui.theme.Red_800
import ir.part.app.intelligentassistant.ui.theme.Text_1
import ir.part.app.intelligentassistant.ui.theme.Text_3
import ir.part.app.intelligentassistant.ui.theme.White
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.file.filename
import ir.part.app.intelligentassistant.utils.ui.UiError
import ir.part.app.intelligentassistant.utils.ui.UiIdle
import ir.part.app.intelligentassistant.utils.ui.isPermissionDeniedPermanently
import ir.part.app.intelligentassistant.utils.ui.navigateToAppSettings
import kotlinx.coroutines.launch
import java.io.File
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun AvaNegarArchiveScreen(
    navHostController: NavHostController,
    archiveViewModel: AvaNegarArchiveViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isFabExpanded by remember { mutableStateOf(false) }

    val processItem = remember {
        mutableStateOf<AvanegarProcessedFileView?>(null)
    }
    val localClipBoardManager = LocalClipboardManager.current

    val fileName = remember {
        mutableStateOf<String?>("")
    }

    val fileUri = remember {
        mutableStateOf<Uri?>(null)
    }

    val (selectedSheet, setSelectedSheet) = remember(calculation = {
        mutableStateOf(
            ArchiveBottomSheetType.ChooseFile
        )
    })

    val isAnyBottomSheetOtherThanUpdate by remember(selectedSheet) {
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

    val uiViewState = archiveViewModel.uiViewState.collectAsStateWithLifecycle(UiIdle).value
    LaunchedEffect(uiViewState) {
        when (uiViewState) {
            is UiError -> {
                Toast.makeText(context, uiViewState.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/*"
    val mimetypes = arrayOf("audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

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
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchOpenFile.launch(intent)
        } else {
            archiveViewModel.putDeniedPermissionToSharedPref(
                isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
            Toast.makeText(
                context,
                AIResource.string.lbl_need_to_access_file_permission,
                Toast.LENGTH_SHORT
            ).show()

        }
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

    LaunchedEffect(archiveViewModel.aiEvent.value) {
        if (archiveViewModel.aiEvent.value == IntelligentAssistantEvent.TokenExpired) {
            setSelectedSheet(ArchiveBottomSheetType.Update)
            modalBottomSheetStateUpdate.show()
        }
    }

    ModalBottomSheetLayout(
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
                        } else if (archiveViewModel.hasDeniedPermissionPermanently()) {
                            navigateToAppSettings(activity = context as Activity)
                            Toast.makeText(
                                context,
                                AIResource.string.msg_access_file_permission_manually,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Asking for permission
                            launcher.launch(permission)
                        }
                    })
                }

                ArchiveBottomSheetType.RenameUploading -> {
                    RenameFileBottomSheetContent(
                        fileName.value.orEmpty(),
                        onValueChange = {
                            fileName.value = it
                        },
                        reNameAction = {
                            archiveViewModel.addFileToUploadingQueue(
                                fileName.value.orEmpty(),
                                fileUri.value
                            )
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
                            //TODO should download update from Bazar or Google play store
                            Toast.makeText(context, "Will Update", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }

                ArchiveBottomSheetType.Rename -> {
                    RenameFile(
                        fileName = fileName.value.orEmpty(),
                        onValueChange = { fileName.value = it },
                        reNameAction = {
                            archiveViewModel.updateTitle(
                                title = fileName.value.orEmpty(),
                                id = processItem.value?.id
                            )
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        }
                    )
                }

                ArchiveBottomSheetType.Detail -> {
                    BottomSheetDetailItem(
                        text = processItem.value?.title.orEmpty(),
                        copyItemAction = {
                            localClipBoardManager.setText(
                                AnnotatedString(
                                    processItem.value?.text.orEmpty()
                                )
                            )
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                            Toast.makeText(
                                context,
                                AIResource.string.lbl_text_save_in_clipboard,
                                Toast.LENGTH_SHORT
                            )
                                .show()
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
                            setSelectedSheet(ArchiveBottomSheetType.Delete)
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
                        onPdfClick = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                convertTextToPdf(
                                    fileName.value.orEmpty(),
                                    text = processItem.value?.text.orEmpty(),
                                    context
                                )
                            }
                        },
                        onWordClick = {},
                        onOnlyTextClick = {}
                    )
                }

                ArchiveBottomSheetType.Delete -> {
                    DeleteFileItemBottomSheet(
                        deleteAction = {
                            archiveViewModel.removeFile(processItem.value?.id)
                            File(
                                processItem.value?.filePath.orEmpty()
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
                        fileName = processItem.value?.title.orEmpty()
                    )
                }

            }
        }
    ) {
        Box(
            modifier = if (archiveViewModel.allArchiveFiles.value.isEmpty())
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            else
                Modifier
                    .fillMaxSize()
                    .paint(painterResource(id = R.drawable.bg_pattern))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                ArchiveAppBar(modifier = Modifier
                    .padding(top = 8.dp),
                    onBackClick = { navHostController.popBackStack() },
                    onSearchClick = {
                        navHostController.navigate(
                            ScreensRouter.AvaNegarSearchScreen.router
                        )
                    })

                if (!archiveViewModel.isNetworkAvailable.value && archiveViewModel.allArchiveFiles.value.isNotEmpty())
                    ErrorBanner(
                        modifier = Modifier.padding(top = 16.dp),
                        errorMessage = stringResource(id = R.string.msg_internet_disconnected)
                    )

                ArchiveBody(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    archiveViewList = archiveViewModel.allArchiveFiles.value,
                    isNetworkAvailable = archiveViewModel.isNetworkAvailable.value,
                    onTryAgainCLick = { archiveViewModel.trackLargeFileResult(it) },
                    onMenuClick = { item ->
                        setSelectedSheet(ArchiveBottomSheetType.Detail)
                        coroutineScope.launch {
                            if (!modalBottomSheetState.isVisible) {
                                modalBottomSheetState.show()
                            } else {
                                modalBottomSheetState.hide()
                            }
                        }
                        processItem.value = item
                        fileName.value = item.title
                    },
                    onItemClick = {
                        navHostController.navigate(
                            ScreensRouter.AvaNegarProcessedArchiveDetailScreen.router.plus(
                                "/$it"
                            )
                        )

                    }
                )
            }

            if (isFabExpanded) {
                Surface(
                    color = MaterialTheme.colors.background.copy(alpha = 0.5f),
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
                openBottomSheet = {
                    setSelectedSheet(ArchiveBottomSheetType.ChooseFile)
                    coroutineScope.launch {
                        if (!modalBottomSheetState.isVisible) {
                            modalBottomSheetState.show()
                        } else {
                            modalBottomSheetState.hide()
                        }
                    }
                })
        }
    }
}

@Composable
private fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_arrow_forward),
                contentDescription = stringResource(id = AIResource.string.desc_back)
            )
        }
        Text(
            text = stringResource(id = AIResource.string.lbl_ava_negar),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        IconButton(onClick = onSearchClick) {
            Icon(
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
    onTryAgainCLick: (String) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit,
    onItemClick: (Int) -> Unit
) {
    if (archiveViewList.isEmpty()) {
        ArchiveEmptyBody(
            modifier = modifier
        )
    } else {
        ArchiveList(
            list = archiveViewList,
            modifier = modifier.padding(vertical = 16.dp),
            isNetworkAvailable = isNetworkAvailable,
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
            .background(Red_800)
    ) {

        Icon(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            painter = painterResource(id = R.drawable.ic_failure_network),
            contentDescription = null,
            tint = Red
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = errorMessage,
            style = MaterialTheme.typography.body2,
            color = Red
        )
    }
}

@Composable
private fun ArchiveEmptyBody(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
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
            color = Text_1,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = AIResource.string.lbl_make_your_first_file),
            style = MaterialTheme.typography.caption,
            color = Text_3,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(58.dp))
        Row(
            modifier = Modifier.weight(0.8f)
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
    onTryAgainCLick: (String) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit,
    onItemClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
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
                    ArchiveProcessedFileElement(
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
                    ArchiveTrackingFileElements(
                        archiveTrackingView = it,
                        isNetworkAvailable = isNetworkAvailable,
                        onItemClick = {},
                        onTryAgainButtonClick = { token ->
                            onTryAgainCLick(token)
                        }
                    )
                }

                is AvanegarUploadingFileView -> {
                    ArchiveUploadingFileElement(
                        archiveUploadingFileView = it,
                        isNetworkAvailable = isNetworkAvailable,
                        onMenuClick = {},
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
    openBottomSheet: () -> Unit
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
                    onClick = openBottomSheet
                ) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_upload),
                        contentDescription = stringResource(id = AIResource.string.desc_upload)
                    )
                }
            }

            FloatingActionButton(
                backgroundColor = if (isFabExpanded) White else MaterialTheme.colors.primary,
                modifier = Modifier
                    .clip(CircleShape),
                onClick = onMainFabClick
            ) {
                Icon(
                    tint = if (isFabExpanded) MaterialTheme.colors.primary else White,
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
                    //TODO implement onCLick
                }) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.ic_mic),
                    contentDescription = stringResource(id = AIResource.string.desc_record)
                )
            }
        }
    }
}
