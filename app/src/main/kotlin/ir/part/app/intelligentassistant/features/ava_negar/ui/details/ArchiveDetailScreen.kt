package ir.part.app.intelligentassistant.features.ava_negar.ui.details

import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.BottomSheetShareDetailItem
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.DeleteFileItemConfirmationBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.RenameFile
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Solid_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_Opacity_15
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Surface_Container_High
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.orZero
import ir.part.app.intelligentassistant.utils.ui.formatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import ir.part.app.intelligentassistant.R as AIResource

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
    val archive = viewModel.archiveFile.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollState = rememberScrollState(0)

    DisposableEffect(context) {
        onDispose {
            viewModel.saveEditedText()
        }
    }

    val mediaPlayer: MediaPlayer? =
        remember(archive) {
            viewModel.archiveFile.value?.filePath?.let {
                MediaPlayer.create(
                    context,
                    Uri.fromFile(
                        File(
                            it
                        )
                    )
                )
            }
        }

    val processItem = viewModel.archiveFile.collectAsStateWithLifecycle()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val fileName = remember {
        mutableStateOf<String?>(null)
    }
    val localClipBoardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()
    val (selectedSheet, setSelectedSheet) = remember(calculation = {
        mutableStateOf(
                ArchiveDetailBottomSheetType.Menu
        )
    })

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
        sheetBackgroundColor = Color.Black,
        sheetContent = {
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
                        onPdfClick = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                convertTextToPdf(
                                    fileName = viewModel.archiveFile.value?.title.orEmpty(),
                                    text = processItem.value?.text.orEmpty(),
                                    context
                                )
                            }
                        },
                        onWordClick = {},
                        onOnlyTextClick = {}
                    )
                }

            }


        }) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
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
                        Toast.makeText(
                            context,
                            AIResource.string.lbl_text_save_in_clipboard,
                            Toast.LENGTH_SHORT
                        ).show()
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
                mediaPlayer = mediaPlayer ?: MediaPlayer(),
                scrollState = scrollState,
                scrollStateValue = scrollState.value
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
    scrollStateValue: Int
) {
    Column(modifier = modifier.padding(paddingValues)) {
        PlayerBody(
            mediaPlayer = mediaPlayer,
            scrollStateValue = scrollStateValue
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
    scrollStateValue: Int
) {
    val color =
        if (scrollStateValue > 0) Color_Surface_Container_High else MaterialTheme.colors.primaryVariant
    val isPlaying = remember {
        mutableStateOf(false)
    }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            if (mediaPlayer.isPlaying) {
                progress = mediaPlayer.currentPosition.toFloat() / 1000
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
                text = formatDuration(mediaPlayer.duration.toLong()),
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
        if (isPlaying.value) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        isPlaying.value = !isPlaying.value
                        mediaPlayer.pause()
                    }
                    .weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colors.primary
                ) {}
                Image(
                    painter = painterResource(id = AIResource.drawable.icon_pause),
                    contentDescription = null
                )
            }

        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        isPlaying.value = !isPlaying.value
                        mediaPlayer.start()
                    }
                    .weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colors.primary
                ) {}
                Image(
                    painter = painterResource(id = AIResource.drawable.icon_play),
                    contentDescription = null
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
            .padding(vertical = 10.dp, horizontal = 15.dp)
    ) {
        MenuDetailsScreenBottomSheetBody(
            title = stringResource(id = AIResource.string.lbl_change_file_name),
            icon = painterResource(
                id = AIResource.drawable.icon_documents
            )
        ) {
            onRenameAction()
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp), color = Color.LightGray
        )
        MenuDetailsScreenBottomSheetBody(
            title = stringResource(id = AIResource.string.lbl_btn_delete_file),
            icon = painterResource(
                id = AIResource.drawable.icon_trash_delete
            )
        ) {
            onRemoveFileAction()
        }
    }
}

@Composable
fun MenuDetailsScreenBottomSheetBody(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onItemClick()
            }
    ) {
        Image(
            painter = icon,
            contentDescription = null
        )
        Text(text = title)
    }
}