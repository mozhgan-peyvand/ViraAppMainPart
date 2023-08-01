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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ir.part.app.intelligentassistant.ui.navigation.ScreensRouter
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.utils.common.file.UploadProgressCallback
import ir.part.app.intelligentassistant.utils.common.file.filename
import kotlinx.coroutines.launch
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

    var progress by remember { mutableStateOf("") }
    var isUploadFinished by remember { mutableStateOf(false) }
    var loading by remember { mutableFloatStateOf(0f) }

    val listener by remember {
        mutableStateOf<UploadProgressCallback>(
            object : UploadProgressCallback {
                override fun onProgress(bytesUploaded: Long, totalBytes: Long, isDone: Boolean) {
                    if (totalBytes <= 0) archiveViewModel.updateIsSaving(true)
                    loading = (bytesUploaded / totalBytes).toFloat()
                    progress = bytesUploaded.toString()
                    isUploadFinished = isDone
                }
            }
        )
    }

    val fileName = remember {
        mutableStateOf<String?>("")
    }
    val fileUri = remember {
        mutableStateOf<Uri?>(null)
    }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val (selectedSheet, setSelectedSheet) = remember(calculation = {
        mutableStateOf(
            ArchiveBottomSheetType.ChooseFile
        )
    })

    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "audio/*"
    val mimetypes = arrayOf("audio/aac", "audio/mpeg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        setSelectedSheet(ArchiveBottomSheetType.Rename)
        coroutineScope.launch {
            if (!modalBottomSheetState.isVisible) {
                modalBottomSheetState.show()
            } else {
                modalBottomSheetState.hide()
            }
        }
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            try {
                fileName.value = it.data?.data?.filename(context)
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

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState, sheetContent = {
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

                ArchiveBottomSheetType.Rename -> {

                    RenameFileBottomSheetContent(
                        fileName.value ?: "",
                        onValueChange = {
                            fileName.value = it
                        },
                        reNameAction = {
                            callBack(fileName.value, fileUri.value, listener)
                            isFabExpanded = false
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ArchiveAppBar(modifier = Modifier
                .padding(top = 8.dp)
                .alpha(if (isFabExpanded) 0.3f else 0.9f),
                isLock = !isFabExpanded,
                onBackClick = {
                    navHostController.popBackStack()
                },
                onSearchClick = { navHostController.navigate(ScreensRouter.AvaNegarSearchScreen.router) })

            if (archiveViewModel.uploadFileState.value != UploadIdle)
                UploadFileSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary.copy(0.8f))
                        .weight(0.2f),
                    uploadFileStatus = archiveViewModel.uploadFileState.value,
                    fileName = fileName.value.orEmpty(),
                    percent = progress,
                    loading = loading,
                    isSavingFile = archiveViewModel.isSavingFile,
                    onRetryCLick = {
                        archiveViewModel.uploadFile(
                            fileName.value.orEmpty(),
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
                    )
                }

                Fabs(isFabExpanded = isFabExpanded,
                    modifier = Modifier.align(Alignment.BottomStart),
                    onMainFabClick = { isFabExpanded = !isFabExpanded },
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
private fun ChooseFileBottomSheetContent(
    onOpenFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_file),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_you_can_only_choose_one_file),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_allowed_format),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 30.dp,
                    vertical = 10.dp
                ),
            onClick = {
                onOpenFile()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = stringResource(id = AIResource.string.lbl_button_upload_new_file))
        }

    }

}

@Composable
private fun RenameFileBottomSheetContent(
    fileName: String,
    onValueChange: (String) -> Unit,
    reNameAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(id = AIResource.string.lbl_change_name),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        Text(
            text = stringResource(id = AIResource.string.lbl_choose_name),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        TextField(value = fileName, onValueChange = {
            onValueChange(it)
        })
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp),
            onClick = {
                reNameAction()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black, contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = stringResource(id = AIResource.string.lbl_save))
        }
    }
}

@Composable
private fun ArchiveList(
    list: List<ArchiveView>,
    isLock: Boolean,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        userScrollEnabled = !isLock,
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = list,
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
                        onMenuClick = {}
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
private fun UploadFileSection(
    modifier: Modifier = Modifier,
    uploadFileStatus: UploadFileStatus,
    fileName: String,
    percent: String,
    loading: Float,
    isSavingFile: Boolean,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        when (uploadFileStatus) {
            is UploadSuccess -> UploadFileSectionSuccess()

            is UploadFailure -> {
                UploadFileSectionFailure(
                    fileName = fileName,
                    onRetryCLick = { onRetryCLick() },
                    onCancelClick = { onCancelClick() })
            }

            is UploadInProgress -> {
                UploadFileSectionInProgress(
                    fileName = fileName,
                    loading = loading,
                    percent = percent,
                    isSavingFile = isSavingFile,
                    onRetryCLick = { onRetryCLick() },
                    onCancelClick = { onCancelClick() }
                )
            }

            is UploadIdle -> {}
        }
    }

}

@Composable
fun ArchiveProcessedFileElement(
    archiveViewProcessed: AvanegarProcessedFileView,
    isLock: Boolean = false,
    onItemClick: (Int) -> Unit,
    onMenuClick: () -> Unit
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
                    onClick = onMenuClick,
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


@Composable
private fun UploadFileSectionInProgress(
    fileName: String,
    loading: Float,
    percent: String,
    isSavingFile: Boolean,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface.copy(0.8f))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = AIResource.string.lbl_uploading_file))

                Text(
                    text = fileName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LoadingIndicator(
                modifier = Modifier,
                loading = loading,
                percent = percent,
                isSavingFile = isSavingFile
            )
        }

        Column(modifier = Modifier.weight(0.3f)) {
            Row {
                IconButton(onClick = { onRetryCLick() }) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_retry),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { onCancelClick() }) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_close),
                        contentDescription = null
                    )
                }
            }
        }
    }

}

@Composable
private fun UploadFileSectionFailure(
    fileName: String,
    onRetryCLick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(end = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = AIResource.string.msg_failure_in_upload))

                Text(
                    text = fileName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Icon(
                    modifier = modifier.padding(end = 8.dp),
                    painter = painterResource(id = AIResource.drawable.ic_failure_network),
                    contentDescription = null
                )
                Text(
                    fontSize = 14.sp,
                    text = stringResource(id = AIResource.string.msg_try_again)
                )
            }
        }

        Column(modifier = Modifier.weight(0.3f)) {
            Row {
                IconButton(onClick = { onRetryCLick() }) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_retry),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { onCancelClick() }) {
                    Icon(
                        painter = painterResource(id = AIResource.drawable.ic_close),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadFileSectionSuccess() {
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            painter = painterResource(AIResource.drawable.ic_tick_circle),
            contentDescription = null
        )
        Text(text = stringResource(id = AIResource.string.msg_upload_is_successfull))
    }

}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    loading: Float,
    percent: String,
    isSavingFile: Boolean
) {

    Box(
        contentAlignment = BottomCenter,
        modifier = modifier
    ) {
        if (isSavingFile)
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(13.dp)
            )
        else
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(13.dp),
                progress = loading,
            )


        Text(
            modifier = Modifier.padding(bottom = 1.dp),
            fontSize = 8.sp,
            text = percent
        )
    }
}

@Preview
@Composable
fun UploadFileSectionPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            UploadFileSection(
                fileName = "FIle Name",
                uploadFileStatus = UploadIdle,
                percent = "82%",
                loading = 0.8f,
                isSavingFile = false,
                onRetryCLick = {},
                onCancelClick = {}
            )
        }
    }
}

@Preview
@Composable
fun UploadFileSectionInProgressPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            UploadFileSectionInProgress("fileName", 0.8f, "48%", false, {}, {})
        }
    }
}

@Preview
@Composable
fun UploadFileSectionFailurePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            UploadFileSectionFailure("fileName", {}, {})
        }
    }
}

@Preview
@Composable
fun LoadingIndicatorPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LoadingIndicator(
                modifier = Modifier,
                loading = 0.8f,
                percent = "40%",
                false
            )
        }
    }
}


@Preview
@Composable
fun UploadFileSectionSuccessPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            UploadFileSectionSuccess()
        }
    }
}

@Preview
@Composable
fun ArchiveListPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveList(listOf(), false)
        }
    }
}

@Preview
@Composable
fun ArchiveElementProcessedFilePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveProcessedFileElement(
                archiveViewProcessed = AvanegarProcessedFileView(
                    0,
                    "title",
                    "text",
                    "0",
                    "",
                    false
                ),
                isLock = false,
                onItemClick = {},
                onMenuClick = {}
            )
        }
    }
}


@Preview
@Composable
fun ArchiveElementTrackingFilePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveTrackingFileElements(
                archiveTrackingView = AvanegarTrackingFileView(
                    "",
                    "",
                    "title",
                    "0"
                ),
                isLock = false,
                onItemClick = {},
                onTryAgainButtonClick = {}
            )
        }
    }
}

@Preview
@Composable
fun AvaNegarArchiveScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvaNegarArchiveScreen(navHostController = rememberNavController())
        }
    }
}