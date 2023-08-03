package ir.part.app.intelligentassistant.ui.screen.archive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.ui.navigation.ScreensRouter
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.BottomSheetDetailItem
import ir.part.app.intelligentassistant.ui.screen.archive.entity.BottomSheetShareDetailItem
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ChooseFileBottomSheetContent
import ir.part.app.intelligentassistant.ui.screen.archive.entity.DeleteFileItemBottomSheet
import ir.part.app.intelligentassistant.ui.screen.archive.entity.RenameFile
import ir.part.app.intelligentassistant.ui.screen.archive.entity.RenameFileBottomSheetContent
import ir.part.app.intelligentassistant.ui.screen.update.ForceUpdateScreen
import ir.part.app.intelligentassistant.utils.common.event.IntelligentAssistantEvent
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.file.filename
import kotlinx.coroutines.launch
import java.io.File
import ir.part.app.intelligentassistant.R as AIResource


@Composable
fun AvaNegarArchiveScreen(
    archiveViewModel: AvaNegarArchiveViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    AvaNegarArchiveBody(
        archiveViewModel,
        navHostController
    ) { fileName, uri, listener ->
        archiveViewModel.uploadFile(fileName.orEmpty(), uri, listener)
    }
}

@Composable
private fun AvaNegarArchiveBody(
    archiveViewModel: AvaNegarArchiveViewModel,
    navHostController: NavHostController,
    callBack: (String?, Uri?, UploadProgressCallback) -> Unit
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

    var progress by remember { mutableStateOf("") }
    var isUploadFinished by remember { mutableStateOf(false) }
    var loading by remember { mutableFloatStateOf(0f) }

    val listener by remember {
        mutableStateOf<UploadProgressCallback>(
            object : UploadProgressCallback {
                override fun onProgress(
                    bytesUploaded: Long,
                    totalBytes: Long,
                    isDone: Boolean
                ) {
                    if (totalBytes <= 0) archiveViewModel.updateIsSaving(
                        true
                    )
                    loading = (bytesUploaded / totalBytes).toFloat()
                    progress = bytesUploaded.toString()
                    isUploadFinished = isDone
                }
            }
        )
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

    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/*"
    val mimetypes = arrayOf("audio/aac", "audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        setSelectedSheet(ArchiveBottomSheetType.RenameUploading)
        coroutineScope.launch {
            if (!modalBottomSheetState.isVisible) {
                modalBottomSheetState.show()
            } else {
                modalBottomSheetState.hide()
            }
        }
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            try {
                fileName.value =
                    it.data?.data?.filename(context) ?: ""
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
            Toast.makeText(
                context,
                AIResource.string.lbl_need_to_access_file_permission,
                Toast.LENGTH_SHORT
            ).show()

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
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) -> {
                                launchOpenFile.launch(intent)
                            }

                            else -> {
                                // Asking for permission
                                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    })
                }

                ArchiveBottomSheetType.RenameUploading -> {
                    RenameFileBottomSheetContent(
                        fileName.value ?: "",
                        onValueChange = {
                            fileName.value = it
                        },
                        reNameAction = {
                            callBack(
                                fileName.value,
                                fileUri.value,
                                listener
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
                            Toast.makeText(context, "Will Update", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ArchiveBottomSheetType.Rename -> {
                    RenameFile(
                        fileName = fileName.value ?: "",
                        onValueChange = { fileName.value = it },
                        reNameAction = {
                            archiveViewModel.updateTitle(
                                title = fileName.value ?: "",
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
                        text = processItem.value?.title ?: "",
                        copyItemAction = {
                            localClipBoardManager.setText(
                                AnnotatedString(
                                    processItem.value?.text ?: ""
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
                                    fileName.value ?: "",
                                    text = processItem.value?.text ?: "",
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
                                processItem.value?.filePath ?: ""
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
                        fileName = processItem.value?.title ?: ""
                    )
                }

            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            ArchiveAppBar(modifier = Modifier
                .padding(top = 8.dp)
                .alpha(if (isFabExpanded) 0.3f else 0.9f),
                isLock = !isFabExpanded,
                onBackClick = {
                    navHostController.popBackStack()
                },
                onSearchClick = {
                    navHostController.navigate(
                        ScreensRouter.AvaNegarSearchScreen.router
                    )
                })

            if (archiveViewModel.uploadFileState.value != UploadIdle)
                UploadFileSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colors.primary.copy(
                                0.8f
                            )
                        )
                        .weight(0.2f),
                    uploadFileStatus = archiveViewModel.uploadFileState.value,
                    fileName = processItem.value?.title.orEmpty(),
                    percent = progress,
                    loading = loading,
                    isSavingFile = archiveViewModel.isSavingFile,
                    onRetryCLick = {
                        archiveViewModel.uploadFile(
                            processItem.value?.title.orEmpty(),
                            fileUri.value,
                            listener
                        )
                    },
                    onCancelClick = { archiveViewModel.cancelDownload() }
                )

            Box(modifier = Modifier.weight(1f)) {

                if (archiveViewModel.allArchiveFiles.value.isEmpty()) {
                    ArchiveBody(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isFabExpanded) 0.3f else 0.9f)
                    )
                } else {
                    ArchiveList(
                        list = archiveViewModel.allArchiveFiles.value,
                        isLock = isFabExpanded,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .alpha(if (isFabExpanded) 0.3f else 0.9f)
                            .pointerInput(Unit) {}
                    ) { item ->
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
                    }
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
}

@Composable
private fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    isLock: Boolean,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            enabled = isLock,
            onClick = { onBackClick() },
        ) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_arrow_forward),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = AIResource.string.lbl_ava_negar),
            Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        IconButton(
            enabled = isLock,
            onClick = onSearchClick
        ) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_search),
                contentDescription = null
            )
        }

    }

}

@Composable
private fun ArchiveBody(
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
            modifier = Modifier.align(Alignment.CenterHorizontally),
            painter = painterResource(id = AIResource.drawable.ic_image_default),
            contentDescription = null
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_dont_have_file),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_make_your_first_file),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = AIResource.drawable.img_arrow),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(100.dp))
    }

}

@Composable
private fun Fabs(
    modifier: Modifier = Modifier,
    isFabExpanded: Boolean,
    onMainFabClick: () -> Unit,
    openBottomSheet: () -> Unit
) {
    Column(
        modifier = modifier.padding(
            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
        )
    ) {

        AnimatedVisibility(visible = isFabExpanded) {

            FloatingActionButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp),
                onClick = {
                    openBottomSheet()
                }) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.ic_upload),
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = isFabExpanded) {

            FloatingActionButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp),
                onClick = {
                    //TODO implement onCLick
                }) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.ic_mic),
                    contentDescription = null
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier.clip(CircleShape),
            onClick = onMainFabClick
        ) {
            Icon(
                painter = painterResource(id = if (isFabExpanded) AIResource.drawable.ic_close else AIResource.drawable.ic_add),
                contentDescription = null
            )
        }
    }
}


@Composable
private fun ArchiveList(
    list: List<ArchiveView>,
    isLock: Boolean,
    modifier: Modifier = Modifier,
    onMenuClick: (AvanegarProcessedFileView) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        userScrollEnabled = !isLock,
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = list,
            key = { item ->
                when (item) {
                    is AvanegarProcessedFileView -> item.id

                    is AvanegarTrackingFileView -> item.token
                    else -> {}
                }
            }) {

            when (it) {
                is AvanegarProcessedFileView -> {
                    ArchiveProcessedFileElement(
                        archiveViewProcessed = it,
                        isLock = isLock,
                        onItemClick = {},
                        onMenuClick = { item ->
                            onMenuClick(item)
                        }
                    )

                }

                is AvanegarTrackingFileView -> {
                    ArchiveTrackingFileElements(
                        archiveTrackingView = it,
                        isLock = isLock,
                        onItemClick = {},
                        onTryAgainButtonClick = {}
                    )

                }
            }
        }
    }
}


@Composable
fun ArchiveProcessedFileElement(
    archiveViewProcessed: AvanegarProcessedFileView,
    isLock: Boolean = false,
    onItemClick: (Int) -> Unit,
    onMenuClick: (AvanegarProcessedFileView) -> Unit
) {
    Card(
        backgroundColor = if (archiveViewProcessed.isSeen) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        enabled = !isLock,
        onClick = { onItemClick(archiveViewProcessed.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically),
                    text = archiveViewProcessed.title
                )

                IconButton(
                    modifier = Modifier.clickable { !isLock },
                    onClick = {
                        onMenuClick(archiveViewProcessed)
                    },
                    enabled = !isLock
                ) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_dots_menu),
                        contentDescription = null
                    )
                }
            }



            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = archiveViewProcessed.text
            )


            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = archiveViewProcessed.createdAt
            )
        }
    }
}

@Composable
private fun ArchiveTrackingFileElements(
    archiveTrackingView: AvanegarTrackingFileView,
    isLock: Boolean,
    onItemClick: (String) -> Unit,
    onTryAgainButtonClick: (String) -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(16.dp),
        enabled = !isLock,
        onClick = { onItemClick(archiveTrackingView.token) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = archiveTrackingView.title
            )

            //TODO Remove it
            Button(
                onClick = { onTryAgainButtonClick(archiveTrackingView.token) },
                enabled = !isLock
            ) {
                Text(text = "تلاش مجدد")
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = AIResource.string.lbl_converting)
            )
        }
    }
}