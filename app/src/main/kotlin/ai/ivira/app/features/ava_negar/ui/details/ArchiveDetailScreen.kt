package ai.ivira.app.features.ava_negar.ui.details

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics
import ai.ivira.app.features.ava_negar.ui.AvanegarAnalytics.AvanegarFileType.Processed
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.ShareDetailItemBottomSheet
import ai.ivira.app.features.ava_negar.ui.details.sheets.MenuDetailsScreenBottomSheet
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.utils.common.file.convertTextToPdf
import ai.ivira.app.utils.common.file.convertTextToTXTFile
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.OnLifecycleEvent
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.sharePdf
import ai.ivira.app.utils.ui.shareTXT
import ai.ivira.app.utils.ui.shareText
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_BG_Solid_2
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.TextAutoSize
import ai.ivira.app.utils.ui.widgets.TextAutoSizeRange
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Min
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TIME_INTERVAL = 2000

@Composable
fun AvaNegarArchiveDetailScreenRoute(
    navController: NavHostController,
    id: Int,
    title: String
) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(AvanegarAnalytics.screenViewArchiveDetail("$id", title))
    }

    AvaNegarArchiveDetailScreen(
        navController = navController,
        itemId = id,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun AvaNegarArchiveDetailScreen(
    navController: NavHostController,
    itemId: Int?,
    viewModel: ArchiveDetailViewModel
) {
    // TODO: seems like savedStateHandle is better
    viewModel.setItemId(itemId.orZero())
    val context = LocalContext.current
    val eventHandler = LocalEventHandler.current
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState(0)
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val focusManager = LocalFocusManager.current
    val playerState by viewModel::playerState

    var isConvertingTxt by rememberSaveable { mutableStateOf(false) }
    var isConvertingPdf by rememberSaveable { mutableStateOf(false) }

    var shouldSharePdf by rememberSaveable { mutableStateOf(false) }
    var shouldShareTxt by rememberSaveable { mutableStateOf(false) }
    var shouldShowKeyBoard by rememberSaveable { mutableStateOf(false) }

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
    var isKeyboardVisible by remember { mutableStateOf(false) }

    var backPressedInterval: Long = 0

    BackHandler {
        if (bottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
            coroutineScope.launch(IO) {
                if (!isConvertingTxt && !isConvertingPdf) {
                    bottomSheetState.hide()
                } else {
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
        } else {
            viewModel.jobConverting?.cancel()
        }
    }

    LaunchedEffect(isConvertingTxt) {
        if (isConvertingTxt) {
            viewModel.jobConverting?.cancel()
            viewModel.jobConverting = coroutineScope.launch(IO) {
                viewModel.fileToShare = convertTextToTXTFile(
                    context = context,
                    text = processItem.value?.text.orEmpty(),
                    fileName = viewModel.archiveFile.value?.title.orEmpty()
                )

                shouldShareTxt = true
                isConvertingTxt = false
            }
        } else {
            viewModel.jobConverting?.cancel()
        }
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

    LaunchedEffect(bottomSheetState.currentValue) {
        if (!bottomSheetState.isVisible) {
            shouldShowKeyBoard = false
            return@LaunchedEffect
        }

        if (selectedSheet.name == ArchiveDetailBottomSheetType.Rename.name) {
            shouldShowKeyBoard = true
        }
    }

    DisposableEffect(context) {
        onDispose {
            viewModel.saveEditedText()
        }
    }

    DisposableEffect(Unit) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            val insets = ViewCompat.getRootWindowInsets(view)

            isKeyboardVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false

            true
        }

        view.viewTreeObserver.addOnPreDrawListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = Color_BG_Bottom_Sheet,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetContent = {
            when (selectedSheet) {
                ArchiveDetailBottomSheetType.Menu -> {
                    // to close keyboard before opening bottomSheet
                    focusManager.clearFocus()
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
                    FileItemConfirmationDeleteBottomSheet(
                        deleteAction = {
                            eventHandler.selectItem(AvanegarAnalytics.selectDeleteFile(Processed))
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
                    RenameFileBottomSheet(
                        fileName = fileName.value ?: processItem.value?.title.orEmpty(),
                        shouldShowKeyBoard = shouldShowKeyBoard,
                        reNameAction = { name ->
                            fileName.value = name
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                            viewModel.updateTitle(
                                title = name,
                                id = viewModel.processItemId.intValue
                            )
                        }
                    )
                }

                ArchiveDetailBottomSheetType.Share -> {
                    ShareDetailItemBottomSheet(
                        isConverting = isConvertingPdf || isConvertingTxt,
                        onPdfClick = {
                            viewModel.saveEditedText()
                            isConvertingPdf = true
                        },
                        onTextClick = {
                            viewModel.saveEditedText()
                            isConvertingTxt = true
                        },
                        onOnlyTextClick = {
                            viewModel.saveEditedText()
                            shareText(
                                context = context,
                                text = processItem.value?.text.orEmpty()
                            )
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        },
                        fileId = itemId?.let { "$it" }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            snackbarHost = {
                if (bottomSheetState.isVisible) {
                    SnackBarWithPaddingBottom(it, true, 500f)
                } else {
                    SnackBar(it)
                }
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
            bottomBar = bottomBar@{
                if (isKeyboardVisible) return@bottomBar

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
                            context.getString(R.string.lbl_text_save_in_clipboard)
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
                playerState = viewModel.playerState,
                scrollState = scrollState,
                scrollStateValue = scrollState.value,
                fileNotExist = viewModel.fileNotExist.value,
                fileNotExistAction = {
                    showMessage(
                        snackbarHostState,
                        coroutineScope,
                        context.getString(R.string.msg_invalid_file)
                    )
                }
            )
        }
    }
}

@Composable
private fun AvaNegarProcessedArchiveDetailTopAppBar(
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
        IconButton(onClick = {
            safeClick {
                onBackAction()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                modifier = Modifier.padding(8.dp),
                contentDescription = stringResource(id = R.string.desc_back)
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

        IconButton(enabled = isRedoEnabled, onClick = {
            safeClick {
                onRedoClick()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_redo,
                contentDescription = stringResource(id = R.string.desc_redo),
                modifier = Modifier.padding(12.dp)
            )
        }

        IconButton(enabled = isUndoEnabled, onClick = {
            safeClick {
                onUndoClick()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_undo,
                contentDescription = stringResource(id = R.string.desc_undo),
                modifier = Modifier.padding(12.dp)
            )
        }

        IconButton(onClick = {
            safeClick {
                onMenuAction()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_dots_menu,
                contentDescription = stringResource(id = R.string.desc_menu),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun AvaNegarProcessedArchiveDetailBottomBar(
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onCopyOnClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(Min)
            .fillMaxWidth()
            .padding(20.dp)
            .background(Color_BG_Solid_2)
    ) {
        TextButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 16.dp,
                start = 10.dp,
                end = 10.dp
            ),
            onClick = {
                safeClick {
                    onCopyOnClick()
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color_Primary_300,
                backgroundColor = Color_Primary_Opacity_15
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViraImage(
                    drawable = R.drawable.ic_copy,
                    contentDescription = stringResource(id = R.string.desc_copy),
                    modifier = modifier.padding(end = 10.dp)
                )
                TextAutoSize(
                    text = stringResource(id = R.string.lbl_btn_copy_text),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300,
                    maxLine = 1,
                    textScale = TextAutoSizeRange(
                        min = 10.sp,
                        max = MaterialTheme.typography.button.fontSize
                    )
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        TextButton(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 16.dp,
                start = 10.dp,
                end = 10.dp
            ),
            onClick = {
                safeClick {
                    onShareClick()
                }
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color_Primary_300,
                backgroundColor = Color_Primary_Opacity_15
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViraImage(
                    drawable = R.drawable.ic_share,
                    contentDescription = stringResource(id = R.string.desc_share),
                    modifier = modifier.padding(end = 10.dp)
                )
                TextAutoSize(
                    text = stringResource(id = R.string.lbl_btn_share_text),
                    style = MaterialTheme.typography.button,
                    color = Color_Primary_300,
                    maxLine = 1,
                    textScale = TextAutoSizeRange(
                        min = 10.sp,
                        max = MaterialTheme.typography.button.fontSize
                    )
                )
            }
        }
    }
}

@Composable
private fun AvaNegarProcessedArchiveDetailBody(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    playerState: VoicePlayerState,
    scrollState: ScrollState,
    scrollStateValue: Int,
    fileNotExist: Boolean,
    fileNotExistAction: () -> Unit
) {
    Column(modifier = modifier.padding(paddingValues)) {
        PlayerBody(
            playerState = playerState,
            scrollStateValue = scrollStateValue,
            fileNotExist = fileNotExist,
            fileNotExistAction = fileNotExistAction
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
private fun PlayerBody(
    modifier: Modifier = Modifier,
    playerState: VoicePlayerState,
    scrollStateValue: Int,
    fileNotExist: Boolean,
    fileNotExistAction: () -> Unit
) {
    val color =
        if (scrollStateValue > 0) Color_Surface_Container_High else MaterialTheme.colors.primaryVariant
    val isPlaying by playerState::isPlaying
    val progress by playerState::progress
    val remainingTime by playerState::remainingTime

    OnLifecycleEvent(
        onPause = {
            if (isPlaying) {
                playerState.stopPlaying()
            }
        }
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = formatDuration(remainingTime.toLong()),
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
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Slider(
                colors = SliderDefaults.colors(
                    activeTrackColor = Color_Primary_300,
                    inactiveTrackColor = Color_Surface_Container_High,
                    thumbColor = Color_White
                ),
                value = progress,
                onValueChange = {
                    playerState.seekTo(it)
                },
                valueRange = 0f .. playerState.duration / 1000.0f,
                modifier = Modifier
                    .weight(4f)
                    .padding(end = 12.dp)
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        IconButton(
            onClick = {
                safeClick {
                    if (fileNotExist) {
                        if (playerState.isPlaying) {
                            playerState.stopPlaying()
                        }
                        fileNotExistAction()
                        return@safeClick
                    }

                    if (isPlaying) {
                        playerState.stopPlaying()
                    } else {
                        playerState.startPlaying()
                    }
                }
            },
            modifier = Modifier.size(46.dp)
        ) {
            if (isPlaying) {
                ViraImage(
                    drawable = R.drawable.ic_pause,
                    contentDescription = stringResource(id = R.string.desc_stop_playing),
                    modifier = modifier.fillMaxSize()
                )
            } else {
                ViraImage(
                    drawable = R.drawable.ic_play,
                    contentDescription = stringResource(id = R.string.desc_start_playing),
                    modifier = modifier.fillMaxSize()
                )
            }
        }
    }
}