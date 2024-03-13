package ai.ivira.app.features.hamahang.ui.detail

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetState
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangSpeakerView
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.OnLifecycleEvent
import ai.ivira.app.utils.ui.convertByteToMB
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersionBetween23And29
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import android.app.Application
import android.media.MediaPlayer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HamahangDetailScreenRoute(navController: NavController) {
    HamahangDetailScreen(
        viewModel = hiltViewModel(),
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun HamahangDetailScreen(
    viewModel: HamahangDetailViewModel,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarState)
    val sheetState = rememberViraBottomSheetState()
    var selectedSheet by rememberSaveable {
        mutableStateOf(HamahangDetailBottomSheetType.DeleteConfirmation)
    }
    val playerState by viewModel::playerState
    val fileInfo by viewModel.processedFile.collectAsStateWithLifecycle()
    val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val writeStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.saveItemToDownloadFolder().also { isSuccess ->
                if (isSuccess) {
                    showMessage(
                        snackBarState,
                        coroutineScope,
                        context.getString(R.string.msg_file_saved_successfully)
                    )
                }
            }
        } else {
            viewModel.putDeniedPermissionToSharedPref(
                permission = writeStoragePermission,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = writeStoragePermission
                )
            )
            showMessage(
                snackBarState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_file_permission)
            )
        }
    }

    val info = fileInfo ?: return

    OnLifecycleEvent(
        onPause = {
            if (playerState.isPlaying) {
                playerState.stopPlaying()
            }
        }
    )

    LaunchedEffect(info) {
        playerState.tryInitWith(
            file = File(info.filePath),
            forcePrepare = true,
            autoStart = true
        )
    }

    HamahangDetailUI(
        fileInfo = info,
        scaffoldState = scaffoldState,
        sheetState = sheetState,
        playerState = playerState,
        selectedSheet = selectedSheet,
        onBackClick = { navigateUp() },
        onDeleteClick = {
            selectedSheet = HamahangDetailBottomSheetType.DeleteConfirmation
            sheetState.show()
        },
        onDeleteConfirmationClick = onClick@{
            viewModel.removeAudio(id = info.id, filePath = info.filePath)
            sheetState.hide()
            navigateUp()
        },
        onShareClick = {
            viewModel.shareItem(context)
        },
        onSaveClick = {
            if (!isSdkVersionBetween23And29() ||
                context.hasPermission(writeStoragePermission)
            ) {
                viewModel.saveItemToDownloadFolder().also { isSuccess ->
                    if (isSuccess) {
                        showMessage(
                            snackBarState,
                            coroutineScope,
                            context.getString(R.string.msg_file_saved_successfully)
                        )
                    }
                }
            } else if (viewModel.hasDeniedPermissionPermanently(writeStoragePermission)) {
                coroutineScope.launch {
                    selectedSheet = HamahangDetailBottomSheetType.FileAccessPermissionDenied
                    sheetState.show()
                }
            } else {
                // Asking for permission
                writeStoragePermissionLauncher.launch(writeStoragePermission)
            }
        },
        navigateToSettings = {
            navigateToAppSettings(activity = context as Activity)
            sheetState.hide()
        }
    )
}

@Composable
private fun HamahangDetailUI(
    fileInfo: HamahangProcessedFileView,
    scaffoldState: ScaffoldState,
    sheetState: ViraBottomSheetState,
    playerState: VoicePlayerState,
    selectedSheet: HamahangDetailBottomSheetType,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirmationClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    navigateToSettings: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                onBackClick = onBackClick,
                onDeleteClick = onDeleteClick,
                onShareClick = onShareClick,
                onSaveClick = onSaveClick
            )
        }
    ) { paddingValue ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            val file = remember(fileInfo) {
                File(fileInfo.filePath)
            }

            ViraImage(
                drawable = fileInfo.speaker.iconRes,
                contentDescription = null,
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth()
                    .weight(0.5f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )

            AudioInfoSection(
                fileName = fileInfo.title,
                fileSize = file.length().toDouble(),
                createdAt = fileInfo.createdAt,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f)
            )

            PlayerSection(
                playerState = playerState,
                onPlayingChanged = {
                    if (!playerState.isPlaying) {
                        playerState.startPlaying()
                    } else {
                        playerState.stopPlaying()
                    }
                },
                onProgressChanged = { progress ->
                    playerState.seekTo(progress)
                },
                modifier = Modifier.weight(0.25f)
            )
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(targetState = selectedSheet) { selected ->
                when (selected) {
                    HamahangDetailBottomSheetType.DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                onDeleteConfirmationClick()
                            },
                            cancelAction = {
                                sheetState.hide()
                            },
                            fileName = ""
                        )
                    }
                    HamahangDetailBottomSheetType.FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(
                            cancelAction = {
                                sheetState.hide()
                            },
                            submitAction = {
                                navigateToSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = {
                safeClick(event = onBackClick)
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                contentDescription = stringResource(id = R.string.desc_back),
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_play_audio),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                safeClick(event = onDeleteClick)
            }
        ) {
            ViraIcon(
                drawable = R.drawable.icon_trash_delete,
                contentDescription = stringResource(id = R.string.desc_btn_delete),
                modifier = Modifier.padding(12.dp)
            )
        }

        IconButton(
            onClick = {
                safeClick(event = onShareClick)
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_share_new,
                contentDescription = stringResource(id = R.string.lbl_share_file),
                modifier = Modifier.padding(12.dp)
            )
        }

        IconButton(
            onClick = {
                safeClick(event = onSaveClick)
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_download_audio,
                contentDescription = stringResource(id = R.string.lbl_save),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun AudioInfoSection(
    fileName: String,
    fileSize: Double,
    createdAt: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = fileName,
            style = MaterialTheme.typography.h6,
            color = Color_Text_1
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(
                text = buildString {
                    append(
                        convertByteToMB(
                            fileSize.orZero()
                        )
                    )
                    append(stringResource(id = R.string.lbl_mb))
                },
                style = MaterialTheme.typography.caption,
                color = Color_Text_3
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = createdAt,
                style = MaterialTheme.typography.caption,
                color = Color_Text_3
            )
        }
    }
}

@Composable
private fun PlayerSection(
    playerState: VoicePlayerState,
    onPlayingChanged: (Boolean) -> Unit,
    onProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val duration by remember(playerState.duration) {
        mutableFloatStateOf(playerState.duration.toFloat() / 1000)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = formatDuration(playerState.duration.toLong()),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.caption,
                color = Color_Text_3,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
            Text(
                text = formatDuration(playerState.elapsedTime.toLong()),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption,
                color = Color_Text_3,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .weight(1f)
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Slider(
                colors = SliderDefaults.colors(
                    activeTrackColor = Color_Primary_300,
                    inactiveTrackColor = Color_Surface_Container_High,
                    thumbColor = Color_White
                ),
                modifier = Modifier.padding(horizontal = 20.dp),
                value = playerState.progress,
                onValueChange = onProgressChanged,
                valueRange = 0f .. duration
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 20.dp, end = 20.dp)
        ) {
            IconButton(
                modifier = Modifier.padding(end = 24.dp),
                onClick = {
                    safeClick {
                        playerState.seekForward()
                    }
                }
            ) {
                ViraImage(
                    drawable = R.drawable.ic_ten_second_after,
                    contentDescription = stringResource(id = R.string.lbl_move_ten_sec_forward)
                )
            }

            IconButton(
                onClick = {
                    safeClick {
                        onPlayingChanged(!playerState.isPlaying)
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        shape = CircleShape,
                        color = Color_Primary
                    )
            ) {
                if (playerState.isPlaying) {
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

            IconButton(
                modifier = Modifier.padding(start = 24.dp),
                onClick = {
                    safeClick {
                        playerState.seekBackward()
                    }
                }
            ) {
                ViraImage(
                    drawable = R.drawable.ic_ten_second_before,
                    contentDescription = stringResource(id = R.string.lbl_move_ten_sec_back)
                )
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun HamahangDetailUIPreview() {
    val mediaPlayer = MediaPlayer()
    val application = LocalContext as Application
    val processed = HamahangProcessedFileView(
        id = 1,
        title = "صوت ۱",
        fileUrl = "/service/voiceConversion/135531d6e06911eead3c0242ac110003.mp3",
        filePath = "",
        inputFilePath = "",
        speaker = HamahangSpeakerView.Chavoshi,
        createdAt = "time",
        isSeen = false
    )

    ViraPreview {
        HamahangDetailUI(
            fileInfo = processed,
            scaffoldState = rememberScaffoldState(),
            sheetState = rememberViraBottomSheetState(),
            playerState = VoicePlayerState(mediaPlayer, application),
            selectedSheet = HamahangDetailBottomSheetType.DeleteConfirmation,
            onBackClick = {},
            onDeleteClick = {},
            onDeleteConfirmationClick = {},
            onShareClick = {},
            onSaveClick = {},
            navigateToSettings = {}
        )
    }
}