package ai.ivira.app.features.hamahang.ui.detail

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetState
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.hamahang.ui.HamahangAnalytics.downloadVoice
import ai.ivira.app.features.hamahang.ui.HamahangAnalytics.screenViewDetails
import ai.ivira.app.features.hamahang.ui.HamahangAnalytics.shareVoice
import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangProcessedFileView
import ai.ivira.app.features.hamahang.ui.archive.model.HamahangSpeakerView
import ai.ivira.app.features.hamahang.ui.detail.components.ThreeMovingCircleAnimation
import ai.ivira.app.features.hamahang.ui.detail.sheets.HamahangRegenerateConfirmationBottomSheet
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.OnLifecycleEvent
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
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
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
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
import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import ai.ivira.app.designsystem.theme.R as ThemeR

@Composable
fun HamahangDetailScreenRoute(navController: NavController) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(screenViewDetails)
    }

    HamahangDetailScreen(
        viewModel = hiltViewModel(),
        navigateUp = {
            navController.navigateUp()
        },
        navigateToNewAudio = { filePath ->
            navController.popBackStack()
            navController.navigate(HamahangScreenRoutes.HamahangNewAudioScreen.createRoute(filePath))
        }
    )
}

@Composable
private fun HamahangDetailScreen(
    viewModel: HamahangDetailViewModel,
    navigateUp: () -> Unit,
    navigateToNewAudio: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val eventHandler = LocalEventHandler.current
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
        context = context,
        scope = coroutineScope,
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
            eventHandler.specialEvent(shareVoice)
            viewModel.shareItem(context)
        },
        onSaveClick = {
            eventHandler.specialEvent(downloadVoice)
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
        },
        regenerateOnClick = {
            if (playerState.isPlaying) {
                playerState.stopPlaying()
            }
            selectedSheet = HamahangDetailBottomSheetType.HamahangRegenerationConfirmation
            sheetState.show()
        },
        regenerateConfirmClick = { filePath ->
            navigateToNewAudio(filePath)
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
    navigateToSettings: () -> Unit,
    regenerateOnClick: () -> Unit,
    context: Context,
    scope: CoroutineScope,
    regenerateConfirmClick: (String) -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ThreeMovingCircleAnimation(
                isPlaying = playerState.isPlaying,
                modifier = Modifier
                    .padding(paddingValue)
                    .fillMaxSize()
                    .zIndex(-1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
                    .zIndex(1f)
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
                        .weight(0.25f),
                    regenerateOnClick = {
                        if (!File(fileInfo.inputFilePath).exists()) {
                            showMessage(
                                scaffoldState.snackbarHostState,
                                scope,
                                context.getString(R.string.lbl_not_possible_to_regenerate_voice)
                            )
                        } else {
                            regenerateOnClick()
                        }
                    }
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
                    HamahangDetailBottomSheetType.HamahangRegenerationConfirmation -> {
                        HamahangRegenerateConfirmationBottomSheet(
                            cancelAction = {
                                sheetState.hide()
                            },
                            regenerateAction = {
                                regenerateConfirmClick(fileInfo.inputFilePath)
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
    regenerateOnClick: () -> Unit,
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Button(
                contentPadding = PaddingValues(8.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
                modifier = Modifier.weight(1f),
                onClick = {
                    safeClick {
                        regenerateOnClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color_Primary_300
                ),
                shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, color = Color_OutLine)
            ) {
                TextWithIcon(
                    text = stringResource(id = R.string.lbl_change_speaker),
                    icon = R.drawable.ic_people,
                    textStyle = MaterialTheme.typography.button,
                    iconTint = Color_Primary_200
                )
            }

            Text(
                text = buildString {
                    append(
                        convertByteToMB(
                            fileSize.orZero()
                        )
                    )
                    append(stringResource(id = R.string.lbl_mb))
                },
                style = MaterialTheme.typography.caption.copy(
                    fontFamily = FontFamily(Font(ThemeR.font.bahij_helvetica_neue_roman))
                ),
                color = Color_Text_3,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextWithIcon(
                text = stringResource(id = R.string.lbl_createAt_with_icon1, createdAt),
                icon = R.drawable.ic_calendar,
                iconTint = Color_Primary_200,
                textStyle = MaterialTheme.typography.caption.copy(color = Color_Text_3),
                textAlign = TextAlign.End

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

// TextWithIcon duplicate 2
@Composable
fun TextWithIcon(
    text: String,
    @DrawableRes icon: Int,
    textStyle: TextStyle = MaterialTheme.typography.caption,
    textAlign: TextAlign = TextAlign.Start,
    iconTint: Color = MaterialTheme.colors.onBackground
) {
    val myId = "inlineContent"
    val annotatedText = buildAnnotatedString {
        val raw = text
        val index = raw.indexOf("[icon]")

        append(raw.substring(0, index))

        appendInlineContent(myId, "[icon]")
        if (index + 6 < raw.length) {
            append(raw.substring(index + 6))
        }
    }

    val inlineContent = mapOf(
        myId to InlineTextContent(
            Placeholder(
                width = 16.sp,
                height = 16.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ViraIcon(
                    drawable = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.8f)
                )
            }
        }
    )

    Text(
        text = annotatedText,
        inlineContent = inlineContent,
        style = textStyle,
        textAlign = textAlign
    )
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
        isSeen = false,
        downloadedBytes = 0,
        downloadingPercent = 0f,
        fileDuration = 0,
        fileSize = 0
    )

    ViraPreview {
        HamahangDetailUI(
            fileInfo = processed,
            scaffoldState = rememberScaffoldState(),
            sheetState = rememberViraBottomSheetState(),
            scope = rememberCoroutineScope(),
            context = LocalContext.current,
            playerState = VoicePlayerState(mediaPlayer, application),
            selectedSheet = HamahangDetailBottomSheetType.DeleteConfirmation,
            onBackClick = {},
            onDeleteClick = {},
            onDeleteConfirmationClick = {},
            onShareClick = {},
            onSaveClick = {},
            navigateToSettings = {},
            regenerateConfirmClick = {},
            regenerateOnClick = {}
        )
    }
}