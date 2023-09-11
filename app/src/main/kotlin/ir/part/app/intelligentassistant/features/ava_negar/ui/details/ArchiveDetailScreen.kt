package ir.part.app.intelligentassistant.features.ava_negar.ui.details

import android.app.Activity
import android.media.MediaPlayer
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.SnackBar
import ir.part.app.intelligentassistant.features.ava_negar.ui.SnackBarWithPaddingBottom
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.BottomSheetShareDetailItem
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.DeleteFileItemConfirmationBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.RenameFile
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.file.convertTextToTXTFile
import ir.part.app.intelligentassistant.utils.common.orZero
import ir.part.app.intelligentassistant.utils.ui.formatDuration
import ir.part.app.intelligentassistant.utils.ui.sharePdf
import ir.part.app.intelligentassistant.utils.ui.shareTXT
import ir.part.app.intelligentassistant.utils.ui.shareText
import ir.part.app.intelligentassistant.utils.ui.showMessage
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Bottom_Sheet
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Solid_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_OutLine
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Surface_Container_High
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ir.part.app.intelligentassistant.R as AIResource

const val TIME_INTERVAL = 2000

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AvaNegarArchiveDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    itemId: Int?,
    viewModel: ArchiveDetailViewModel = hiltViewModel()
) {
    viewModel.setItemId(itemId.orZero())
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState(0)
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val focusManager = LocalFocusManager.current

    var isConvertingTxt by rememberSaveable { mutableStateOf(false) }
    var isConvertingPdf by rememberSaveable { mutableStateOf(false) }

    var shouldSharePdf by rememberSaveable { mutableStateOf(false) }
    var shouldShareTxt by rememberSaveable { mutableStateOf(false) }
    val shouldShowKeyBoard = rememberSaveable { mutableStateOf(false) }

    val processItem = viewModel.archiveFile.collectAsStateWithLifecycle()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { !isConvertingPdf && !isConvertingTxt }
    )
    val fileName = rememberSaveable { mutableStateOf<String?>(null) }

    val localClipBoardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()
    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(ArchiveDetailBottomSheetType.Menu)
    }

    var backPressedInterval: Long = 0

    BackHandler {
        if (bottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
            coroutineScope.launch(IO) {
                if (!isConvertingTxt && !isConvertingPdf)
                    bottomSheetState.hide()
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
                            bottomSheetState.hide()
                        }
                    }
                }
            }
        } else {
            focusManager.clearFocus()
            navController.navigateUp()
        }
    }

    LaunchedEffect(isConvertingPdf) {
        if (isConvertingPdf) {

            viewModel.jobConverting?.cancel()
            viewModel.jobConverting = coroutineScope.launch(IO) {
                viewModel.fileToShare = convertTextToPdf(
                    context = context,
                    text = processItem.value?.text.orEmpty(),
                    fileName = viewModel.archiveFile.value?.title.orEmpty()
                )

                shouldSharePdf = true
                isConvertingPdf = false
            }

        } else viewModel.jobConverting?.cancel()
    }

    LaunchedEffect(isConvertingTxt) {
        if (isConvertingTxt) {

            viewModel.jobConverting?.cancel()
            viewModel.jobConverting = coroutineScope.launch(IO) {
                viewModel.fileToShare = convertTextToTXTFile(
                    context = context,
                    text = processItem.value?.text.orEmpty(),
                    fileName = fileName.value.orEmpty()
                )

                shouldShareTxt = true
                isConvertingTxt = false
            }

        } else viewModel.jobConverting?.cancel()

    }

    LaunchedEffect(shouldSharePdf) {
        if (shouldSharePdf) {
            bottomSheetState.hide()
            viewModel.fileToShare?.let {
                sharePdf(context = context, file = it)
                shouldSharePdf = false
            }
        }
    }

    LaunchedEffect(shouldShareTxt) {
        if (shouldShareTxt) {
            bottomSheetState.hide()
            viewModel.fileToShare?.let {
                shareTXT(context = context, file = it)
                shouldShareTxt = false
            }
        }
    }

    LaunchedEffect(bottomSheetState.targetValue) {

        if (bottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
            if (selectedSheet.name == ArchiveDetailBottomSheetType.Rename.name) {
                (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                shouldShowKeyBoard.value = true
            }
        } else {
            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            shouldShowKeyBoard.value = false
        }
    }

    DisposableEffect(context) {
        onDispose {
            viewModel.saveEditedText()
            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = Color_BG_Bottom_Sheet,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetContent = {
            //to close keyboard before opening bottomSheet
            focusManager.clearFocus()
            when (selectedSheet) {

                ArchiveDetailBottomSheetType.Menu -> {
                    MenuDetailsScreenBottomSheet(
                        onRenameAction = {
                            setSelectedSheet(ArchiveDetailBottomSheetType.Rename)
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                if (!bottomSheetState.isVisible) {
                                    bottomSheetState.show()
                                } else {
                                    bottomSheetState.hide()
                                }
                            }
                        },
                        onRemoveFileAction = {
                            setSelectedSheet(ArchiveDetailBottomSheetType.Delete)
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                if (!bottomSheetState.isVisible) {
                                    bottomSheetState.show()
                                } else {
                                    bottomSheetState.hide()
                                }
                            }
                        }
                    )
                }

                ArchiveDetailBottomSheetType.Delete -> {
                    DeleteFileItemConfirmationBottomSheet(
                        deleteAction = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                navController.popBackStack()
                            }
                            viewModel.removeFile(viewModel.processItemId.intValue)
                        },
                        cancelAction = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        },
                        fileName = viewModel.archiveFile.value?.title ?: ""
                    )
                }

                ArchiveDetailBottomSheetType.Rename -> {
                    RenameFile(
                        fileName = fileName.value
                            ?: viewModel.archiveFile.value?.title ?: "",
                        onValueChange = {
                            fileName.value = it
                        },
                        shouldShowKeyBoard = shouldShowKeyBoard.value,
                        reNameAction = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                            viewModel.updateTitle(
                                title = fileName.value,
                                id = viewModel.processItemId.intValue
                            )
                        })
                }

                ArchiveDetailBottomSheetType.Share -> {
                    BottomSheetShareDetailItem(
                        isConverting = isConvertingPdf || isConvertingTxt,
                        onPdfClick = { isConvertingPdf = true },
                        onTextClick = { isConvertingTxt = true },
                        onOnlyTextClick = {
                            shareText(
                                context = context,
                                text = processItem.value?.text.orEmpty()
                            )
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        }
                    )
                }
            }


        }) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            snackbarHost = {
                if (bottomSheetState.isVisible)
                    SnackBarWithPaddingBottom(it, true, 500f)
                else SnackBar(it)
            },
            topBar = {
                AvaNegarProcessedArchiveDetailTopAppBar(
                    title = processItem.value?.title.orEmpty(),
                    isUndoEnabled = viewModel.canUndo(),
                    isRedoEnabled = viewModel.canRedo(),
                    onUndoClick = {
                        keyboardController?.hide()
                        viewModel.undo()
                    },
                    onRedoClick = {
                        keyboardController?.hide()
                        viewModel.redo()
                    },
                    onBackAction = {
                        // TODO: pass action not navController
                        navController.popBackStack()
                    },
                    onMenuAction = {
                        setSelectedSheet(ArchiveDetailBottomSheetType.Menu)
                        coroutineScope.launch {
                            if (!bottomSheetState.isVisible) {
                                bottomSheetState.show()
                            } else {
                                bottomSheetState.hide()
                            }
                        }
                    },
                    scrollStateValue = scrollState.value
                )
            },
            bottomBar = {
                AvaNegarProcessedArchiveDetailBottomBar(
                    onShareClick = {
                        setSelectedSheet(ArchiveDetailBottomSheetType.Share)
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            if (!bottomSheetState.isVisible) {
                                bottomSheetState.show()
                            } else {
                                bottomSheetState.hide()
                            }
                        }

                    },
                    onCopyOnClick = {
                        localClipBoardManager.setText(
                            AnnotatedString(
                                processItem.value?.text.orEmpty()
                            )
                        )

                        showMessage(
                            snackbarHostState,
                            coroutineScope,
                            context.getString(AIResource.string.lbl_text_save_in_clipboard)
                        )
                    }
                )
            }
        ) { padding ->
            AvaNegarProcessedArchiveDetailBody(
                paddingValues = padding,
                text = viewModel.textBody.value,
                onTextChange = {
                    viewModel.addTextToList(it)
                },
                mediaPlayer = viewModel.mediaPlayer,
                scrollState = scrollState,
                scrollStateValue = scrollState.value,
                stopMediaPlayer = {
                    viewModel.stopMediaPlayer()
                },
                startMediaPlayer = {
                    viewModel.startMediaPlayer()
                }
            )
        }
    }
}

@Composable
fun AvaNegarProcessedArchiveDetailTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onBackAction: () -> Unit,
    onMenuAction: () -> Unit,
    scrollStateValue: Int
) {
    val color =
        if (scrollStateValue > 0) Color_Surface_Container_High else MaterialTheme.colors.primaryVariant
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackAction() }
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(id = AIResource.drawable.ic_arrow_forward),
                contentDescription = stringResource(id = AIResource.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.subtitle2,
            color = Color_White
        )

        Spacer(modifier = Modifier.size(8.dp))

        IconButton(
            enabled = isRedoEnabled,
            onClick = { onRedoClick() }) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = AIResource.drawable.ic_redo),
                contentDescription = stringResource(id = AIResource.string.desc_redo)
            )
        }

        IconButton(
            enabled = isUndoEnabled,
            onClick = { onUndoClick() }) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = AIResource.drawable.ic_undo),
                contentDescription = stringResource(id = AIResource.string.desc_undo)
            )
        }

        IconButton(
            onClick = { onMenuAction() }
        ) {
            Icon(
                modifier = Modifier.padding(12.dp),
                painter = painterResource(id = AIResource.drawable.ic_dots_menu),
                contentDescription = stringResource(id = AIResource.string.desc_menu)
            )
        }
    }
}

@Composable
fun AvaNegarProcessedArchiveDetailBottomBar(
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onCopyOnClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(Color_BG_Solid_2)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                top = 14.dp, bottom = 14.dp, start = 19.dp, end = 23.dp
            ),
            onClick = { onCopyOnClick() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color_Primary_300,
                backgroundColor = Color_Primary_Opacity_15
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = AIResource.drawable.ic_copy),
                    contentDescription = stringResource(id = AIResource.string.desc_copy),
                    modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = AIResource.string.lbl_btn_copy_text),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                top = 14.dp, bottom = 14.dp, start = 19.dp, end = 23.dp
            ),
            onClick = { onShareClick() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color_Primary_300,
                backgroundColor = Color_Primary_Opacity_15
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = AIResource.drawable.ic_share),
                    contentDescription = stringResource(id = AIResource.string.desc_share),
                    modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = AIResource.string.lbl_btn_share_text),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300
                )
            }
        }
    }
}

@Composable
fun AvaNegarProcessedArchiveDetailBody(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    mediaPlayer: MediaPlayer,
    scrollState: ScrollState,
    scrollStateValue: Int,
    startMediaPlayer: () -> Unit,
    stopMediaPlayer: () -> Unit
) {
    Column(modifier = modifier.padding(paddingValues)) {
        PlayerBody(
            mediaPlayer = mediaPlayer,
            scrollStateValue = scrollStateValue,
            startMediaPlayer = startMediaPlayer,
            stopMediaPlayer = stopMediaPlayer
        )
        TextField(
            value = text,
            onValueChange = { onTextChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .verticalScroll(scrollState),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun PlayerBody(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer,
    scrollStateValue: Int,
    startMediaPlayer: () -> Unit,
    stopMediaPlayer: () -> Unit
) {
    val color =
        if (scrollStateValue > 0) Color_Surface_Container_High else MaterialTheme.colors.primaryVariant
    val isPlaying = remember {
        mutableStateOf(false)
    }
    var remainingTime by rememberSaveable { mutableLongStateOf(0) }
    var progress by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = Unit) {
        remainingTime = mediaPlayer.duration.toLong()
        while (isActive) {
            if (mediaPlayer.isPlaying) {
                progress = mediaPlayer.currentPosition.toFloat() / 1000
                remainingTime =
                    (mediaPlayer.duration - mediaPlayer.currentPosition).toLong()
                mediaPlayer.setOnCompletionListener {
                    isPlaying.value = false
                    progress = 0f
                }
            }
            delay(1000)
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = formatDuration(remainingTime),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption
            )
            Text(
                text = "-",
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) { // TODO: not working
            Slider(
                colors = SliderDefaults.colors(
                    activeTrackColor = Color_Primary_300,
                    inactiveTrackColor = Color_Surface_Container_High,
                    thumbColor = Color_White
                ),
                value = progress,
                onValueChange = {
                    progress = it
                    mediaPlayer.seekTo((it * 1000).toInt())
                },
                valueRange = 0f..mediaPlayer.duration.toFloat() / 1000,
                modifier = Modifier
                    .weight(4f)
                    .padding(end = 12.dp)
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        IconButton(
            onClick = {
                isPlaying.value = !isPlaying.value
                if (isPlaying.value) {
                    startMediaPlayer()
                } else {
                    stopMediaPlayer()
                }
            },
            modifier = Modifier.size(46.dp)
        ) {
            if (isPlaying.value) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = stringResource(id = R.string.desc_stop_playing),
                    modifier = modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = stringResource(id = R.string.desc_start_playing),
                    modifier = modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
fun MenuDetailsScreenBottomSheet(
    modifier: Modifier = Modifier,
    onRenameAction: () -> Unit,
    onRemoveFileAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        TextButton(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 12.dp),
            contentPadding = PaddingValues(12.dp),
            onClick = onRenameAction
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.icon_documents),
                    contentDescription = null,
                    tint = Color_Text_1
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = AIResource.string.lbl_change_file_name),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Text_3
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color_OutLine
        )

        Spacer(modifier = Modifier.size(12.dp))

        TextButton(
            modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
            contentPadding = PaddingValues(12.dp),
            onClick = onRemoveFileAction
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = AIResource.drawable.icon_trash_delete),
                    contentDescription = null,
                    tint = Color_Red
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(id = AIResource.string.lbl_btn_delete_file),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Red
                )
            }
        }
    }
}