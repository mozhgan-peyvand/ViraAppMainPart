package ir.part.app.intelligentassistant.ui.screen.details

import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.ui.screen.archive.entity.BottomSheetShareDetailItem
import ir.part.app.intelligentassistant.ui.screen.archive.entity.DeleteFileItemBottomSheet
import ir.part.app.intelligentassistant.ui.screen.archive.entity.RenameFile
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.orZero
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import ir.part.app.intelligentassistant.R as AIResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AvaNegarProcessedArchiveDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    itemId: Int?,
    viewModel: AvaNegarProcessedDetailViewModel = hiltViewModel()
) {
    viewModel.setItemId(itemId.orZero())
    val context = LocalContext.current
    val archive = viewModel.archiveFile.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

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
            DetailBottomSheetState.Menu
        )
    })

    ModalBottomSheetLayout(sheetState = bottomSheetState, sheetContent = {
        when (selectedSheet) {

            DetailBottomSheetState.Menu -> {
                MenuDetailsScreenBottomSheet(
                    onRenameAction = {
                        setSelectedSheet(DetailBottomSheetState.Rename)
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
                        setSelectedSheet(DetailBottomSheetState.Delete)
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

            DetailBottomSheetState.Delete -> {
                DeleteFileItemBottomSheet(
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

            DetailBottomSheetState.Rename -> {
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

            DetailBottomSheetState.Share -> {
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
                        setSelectedSheet(DetailBottomSheetState.Menu)
                        coroutineScope.launch {
                            if (!bottomSheetState.isVisible) {
                                bottomSheetState.show()
                            } else {
                                bottomSheetState.hide()
                            }
                        }
                    }
                )
            },
            bottomBar = {
                AvaNegarProcessedArchiveDetailBottomBar(
                    onShareClick = {
                        setSelectedSheet(DetailBottomSheetState.Share)
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
                mediaPlayer = mediaPlayer ?: MediaPlayer()
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
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onBackAction() }) {
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = stringResource(id = AIResource.string.desc_menu)
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            enabled = isRedoEnabled,
            onClick = { onRedoClick() }
        ) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_arrow_redo),
                contentDescription = stringResource(id = AIResource.string.desc_redo)
            )
        }

        IconButton(
            enabled = isUndoEnabled,
            onClick = { onUndoClick() }
        ) {
            Icon(
                painter = painterResource(id = AIResource.drawable.ic_arrow_undo),
                contentDescription = stringResource(id = AIResource.string.desc_undo)
            )
        }

        IconButton(onClick = { onMenuAction() }) {
            Icon(
                painter = painterResource(id = AIResource.drawable.icon_container),
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
    Row(modifier = modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            onClick = { onCopyOnClick() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black, backgroundColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row {
                Image(
                    painter = painterResource(id = AIResource.drawable.icon_copy),
                    contentDescription = stringResource(id = AIResource.string.desc_copy)
                )
                Text(text = stringResource(id = AIResource.string.lbl_btn_copy_text))
            }
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            onClick = { onShareClick() },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black, backgroundColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Row {
                Image(
                    painter = painterResource(id = AIResource.drawable.icon_share),
                    contentDescription = stringResource(id = AIResource.string.desc_share)
                )
                Text(text = stringResource(id = AIResource.string.lbl_btn_share_text))
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
    mediaPlayer: MediaPlayer
) {
    Column(modifier = modifier.padding(paddingValues)) {
        Row {
            PlayerBody(mediaPlayer = mediaPlayer)
        }
        TextField(
            value = text,
            onValueChange = { onTextChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )
    }
}

@Composable
fun PlayerBody(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer
) {

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
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.End
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Slider(
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Black
                ),
                value = progress,
                onValueChange = {
                    progress = it
                    mediaPlayer.seekTo((it * 1000).toInt())
                },
                valueRange = 0f..mediaPlayer.duration.toFloat() / 1000
            )
            Row {
                Text(text = ((mediaPlayer.currentPosition.toFloat() / 1000)).toString())
                Text(text = "/")
                Text(
                    text = TimeUnit.SECONDS.toMinutes(mediaPlayer.duration.toLong())
                        .toString()
                )
            }
        }
        if (isPlaying.value) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable {
                    isPlaying.value = !isPlaying.value
                    mediaPlayer.pause()
                }
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color.Black
                ) {}

                Image(
                    painter = painterResource(id = AIResource.drawable.icon_pause),
                    contentDescription = null
                )
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable {
                    isPlaying.value = !isPlaying.value
                    mediaPlayer.start()
                }
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color.Black
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