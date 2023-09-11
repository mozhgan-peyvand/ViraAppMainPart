package ir.part.app.intelligentassistant.features.ava_negar.ui.record

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.RenameFileBottomSheetContent
import ir.part.app.intelligentassistant.features.ava_negar.ui.record.RecordFileResult.Companion.FILE_NAME
import ir.part.app.intelligentassistant.utils.ui.formatAsDuration
import ir.part.app.intelligentassistant.utils.ui.hide
import ir.part.app.intelligentassistant.utils.ui.hideAndShow
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_On_Surface_Variant
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_200
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Primary_300
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Red
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Surface_Container_High
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.IntelligentAssistantTheme
import kotlinx.coroutines.CoroutineScope
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

// check for audio permission here!
@Composable
fun AvaNegarVoiceRecordingScreen(
    navController: NavHostController,
    viewModel: VoiceRecordingViewModel = hiltViewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var bottomSheetContentType by rememberSaveable(
        saver = Saver(
            save = { it.value.name },
            restore = { name ->
                mutableStateOf(VoiceRecordingBottomSheetType.values().first { it.name == name })
            }
        )
    ) {
        mutableStateOf(VoiceRecordingBottomSheetType.BackConfirm)
    }
    val coroutineScope = rememberCoroutineScope()

    var state by rememberSaveable(
        saver = Saver(
            save = { it.value.serialize() },
            restore = { mutableStateOf(VoiceRecordingViewState.deserialize(it)) }
        )
    ) { mutableStateOf<VoiceRecordingViewState>(VoiceRecordingViewState.Idle) }
    val timer by viewModel.timer.collectAsState()

    val recorder by viewModel::recorder
    val playerState by viewModel::playerState

    if (state is VoiceRecordingViewState.Stopped) {
        playerState.tryInitWith(recorder.currentFile)
    }

    val shouldShowKeyBoard = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(bottomSheetState.targetValue) {

        if (bottomSheetState.targetValue != ModalBottomSheetValue.Hidden) {
            if (bottomSheetContentType.name == VoiceRecordingBottomSheetType.ConvertToTextConfirmation.name)
                shouldShowKeyBoard.value = true
        } else {
            shouldShowKeyBoard.value = false
        }
    }

    BackHandler(state != VoiceRecordingViewState.Idle) {
        // BackHandler: Duplicate 1
        handleBackClick(
            state = state,
            coroutineScope = coroutineScope,
            bottomSheetState = bottomSheetState,
            updateBottomSheetType = {
                bottomSheetContentType = it
            },
            goBack = {
                recorder.removeCurrentRecording()
                navController.popBackStack()
            }
        )
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (bottomSheetContentType) {
                VoiceRecordingBottomSheetType.BackConfirm -> {
                    BackToArchiveListConfirmationBottomSheet(
                        actionConvertFile = {
                            bottomSheetContentType =
                                VoiceRecordingBottomSheetType.ConvertToTextConfirmation
                            bottomSheetState.hideAndShow(coroutineScope)
                        },
                        actionDeleteFile = {
                            recorder.removeCurrentRecording()
                            bottomSheetState.hide(coroutineScope)
                            navController.popBackStack()
                        }
                    )
                }

                VoiceRecordingBottomSheetType.StartAgainConfirmation -> {
                    StartAgainBottomSheet(
                        actionCancel = {
                            bottomSheetState.hide(coroutineScope)
                        },
                        actionStartAgain = {
                            playerState.reset()
                            bottomSheetState.hide(coroutineScope)
                            recorder.removeCurrentRecording()
                            // RecordStart: Duplicate1
                            recorder.start("rec_${System.currentTimeMillis()}_${SystemClock.elapsedRealtime()}")
                            viewModel.resetTimer()
                            viewModel.startTimer()
                            state = VoiceRecordingViewState.Recording(false)
                        }
                    )
                }

                VoiceRecordingBottomSheetType.ConvertToTextConfirmation -> {
                    RenameFileBottomSheetContent(
                        fileName = "",
                        shouldShowKeyBoard = shouldShowKeyBoard.value,
                        renameAction = { name ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(
                                    FILE_NAME,
                                    recorder.currentFile?.absolutePath?.let {
                                        RecordFileResult(
                                            title = name,
                                            filepath = it
                                        )
                                    }
                                )
                            navController.popBackStack()
                        }
                    )
                }
            }
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = MaterialTheme.colors.background.copy(alpha = 0.5f),
        sheetBackgroundColor = Color_BG
    ) {
        Column {
            VoiceRecordingTopAppBar(
                onBackClick = {
                    // BackHandler: Duplicate 2
                    handleBackClick(
                        state = state,
                        coroutineScope = coroutineScope,
                        bottomSheetState = bottomSheetState,
                        updateBottomSheetType = {
                            bottomSheetContentType = it
                        },
                        goBack = {
                            recorder.removeCurrentRecording()
                            navController.popBackStack()
                        }
                    )
                }
            )

            val isStopped: Boolean
            val isRecording: Boolean
            val hasPaused: Boolean
            if (state is VoiceRecordingViewState.Recording) {
                isStopped = false
                isRecording = true
                hasPaused = (state as VoiceRecordingViewState.Recording).hasPaused
            } else {
                isRecording = false
                hasPaused = state is VoiceRecordingViewState.Paused
                isStopped = state is VoiceRecordingViewState.Stopped
            }
            VoiceRecordingBody(
                isStopped = isStopped,
                isRecording = isRecording,
                hasPaused = hasPaused,
                timer = timer,
                playerState = playerState,
                startRecord = {
                    if (state is VoiceRecordingViewState.Stopped) {
                        bottomSheetContentType =
                            VoiceRecordingBottomSheetType.StartAgainConfirmation
                        bottomSheetState.hideAndShow(coroutineScope)
                    } else {
                        if (state is VoiceRecordingViewState.Paused) {
                            recorder.resume()
                        } else {
                            // TODO: handle return value
                            // What if we don't have record permission
                            // RecordStart: Duplicate2
                            recorder.start("rec_${System.currentTimeMillis()}_${SystemClock.elapsedRealtime()}")
                        }
                        viewModel.startTimer()
                        state = VoiceRecordingViewState.Recording(
                            hasPaused = state is VoiceRecordingViewState.Paused
                        )
                    }
                },
                pauseRecord = {
                    viewModel.pauseTimer()
                    recorder.pause()
                    state = VoiceRecordingViewState.Paused
                },
                stopRecord = {
                    viewModel.pauseTimer()
                    recorder.stop()
                    state = VoiceRecordingViewState.Stopped
                },
                convertToText = {
                    bottomSheetContentType = VoiceRecordingBottomSheetType.ConvertToTextConfirmation
                    bottomSheetState.hideAndShow(coroutineScope)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun VoiceRecordingTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(text = stringResource(id = R.string.lbl_record_voice))
    }
}

@Composable
fun VoiceRecordingBody(
    isRecording: Boolean,
    hasPaused: Boolean,
    isStopped: Boolean,
    timer: Int,
    playerState: VoicePlayerState,
    startRecord: () -> Unit,
    stopRecord: () -> Unit,
    pauseRecord: () -> Unit,
    convertToText: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.fillMaxWidth()) {
        VoiceRecordingPreviewSection(
            isRecording = isRecording,
            isStopped = isStopped,
            playerState = playerState,
            modifier = Modifier.weight(2.5f)
        )
        VoiceRecordingHintSection(
            isRecording = isRecording,
            hasPaused = hasPaused,
            isStopped = isStopped,
            time = timer,
            modifier = modifier.weight(1f)
        )
        VoiceRecordingControlsSection(
            isRecording = isRecording,
            hasPaused = hasPaused,
            isStopped = isStopped,
            startRecord = startRecord,
            pauseRecord = pauseRecord,
            stopRecord = stopRecord,
            onConvertClick = convertToText
        )
    }
}

@Composable
fun VoiceRecordingPreviewSection(
    isRecording: Boolean,
    isStopped: Boolean,
    playerState: VoicePlayerState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isStopped) Modifier.padding(
                    bottom = 54.dp,
                    start = 16.dp,
                    end = 16.dp
                ) else Modifier.clip(CircleShape)
            )
    ) {
        if (isStopped) {
            val progress by playerState::progress
            val isPlaying by playerState::isPlaying

            VoicePlayerComponent(
                duration = playerState.duration / 1000,
                progress = progress,
                onProgressChanged = {
                    playerState.seekTo(it)
                },
                isPlaying = isPlaying,
                onPlayingChanged = {
                    if (!isPlaying) {
                        playerState.startPlaying()
                    } else {
                        playerState.stopPlaying()
                    }
                }
            )
        } else {
            AndroidView(
                factory = {
                    GifImageView(context).also {
                        it.setImageDrawable(
                            GifDrawable(context.assets, "sirilike.gif").also { gif ->
                                gif.pause()
                                gif.seekTo(0)
                            }
                        )
                    }
                },
                update = {
                    if (isRecording) {
                        (it.drawable as GifDrawable).start()
                    } else {
                        (it.drawable as GifDrawable).stop()
                    }
                },
                modifier = Modifier.size(236.dp)
            )
        }
    }
}

@Composable
fun VoiceRecordingHintSection(
    isRecording: Boolean,
    hasPaused: Boolean,
    isStopped: Boolean,
    modifier: Modifier = Modifier,
    time: Int = 0,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        if (isStopped) {
            Text(
                text = stringResource(id = R.string.lbl_recording_finished),
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.size(12.dp))
            TextWithIcon(
                text = R.string.lbl_press_to_convert_to_text,
                icon = R.drawable.ic_repeat
            )
        } else if (!isRecording) {
            if (hasPaused) {
                Text(
                    text = stringResource(id = R.string.lbl_recording_paused),
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.size(12.dp))
            }
            TextWithIcon(
                text = if (hasPaused) R.string.lbl_press_to_continue_recording else R.string.lbl_press_to_start_recording,
                icon = R.drawable.ic_mic_2
            )
        } else {
            Text(
                text = stringResource(id = R.string.lbl_recording_in_progress),
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.size(12.dp))
            Row {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
                    Text(text = "02:00:00", color = Color_Red)
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "/")
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(time.formatAsDuration(true))
                }
            }
        }
    }
}

@Composable
fun VoiceRecordingControlsSection(
    isRecording: Boolean,
    hasPaused: Boolean,
    isStopped: Boolean,
    startRecord: () -> Unit,
    stopRecord: () -> Unit,
    pauseRecord: () -> Unit,
    onConvertClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (isRecording || hasPaused || isStopped) {
            IconButton(
                onClick = { onConvertClick() },
                enabled = isStopped
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = Color_Card, // TODO: use the new bottomSheet color
                    shape = CircleShape
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_repeat
                            ),
                            contentDescription = stringResource(R.string.desc_convert_to_text),
                            tint = if (isStopped) Color_Primary_200 else Color_On_Surface_Variant,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }

        RecordingAnimation(
            isRecording = isRecording,
            onRecordClick = { startRecord() },
            modifier = Modifier.size(170.dp)
        )

        if (isRecording || hasPaused || isStopped) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .then(
                        if (isStopped) Modifier
                        else Modifier.clickable {
                            if (isRecording) pauseRecord() else stopRecord()
                        }
                    ),
                color = Color_Card, // TODO: use the new bottomSheet color
                shape = CircleShape,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        painter = painterResource(
                            id = if (isRecording) R.drawable.ic_pause_circle else R.drawable.ic_stop_circled
                        ),
                        contentDescription = if (isRecording) stringResource(R.string.desc_pause_recording)
                        else if (!isStopped) stringResource(R.string.desc_stop_recording)
                        else null,
                        tint = if (isStopped) Color_On_Surface_Variant else Color_Primary_200,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

}

@Composable
fun VoicePlayerComponent(
    duration: Int,
    progress: Float,
    onProgressChanged: (Float) -> Unit,
    isPlaying: Boolean,
    onPlayingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(shape = MaterialTheme.shapes.medium) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = duration.formatAsDuration(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.size(12.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Slider(
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color_Primary_300,
                        inactiveTrackColor = Color_Surface_Container_High,
                        thumbColor = Color_White
                    ),
                    value = progress,
                    onValueChange = onProgressChanged,
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            IconButton(
                onClick = { onPlayingChanged(!isPlaying) },
                modifier = Modifier.size(46.dp)
            ) {
                if (isPlaying) {
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
}

private fun handleBackClick(
    state: VoiceRecordingViewState,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    updateBottomSheetType: (VoiceRecordingBottomSheetType) -> Unit,
    goBack: () -> Unit
) {
    if (bottomSheetState.isVisible) {
        bottomSheetState.hide(coroutineScope)
    } else {
        when (state) {
            VoiceRecordingViewState.Idle -> {
                goBack()
            }

            is VoiceRecordingViewState.Recording -> {
                // ignore back presses while recording
            }

            VoiceRecordingViewState.Paused,
            VoiceRecordingViewState.Stopped -> {
                updateBottomSheetType(VoiceRecordingBottomSheetType.BackConfirm)
                bottomSheetState.hideAndShow(coroutineScope)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvaNegarVoiceRecordingScreen(rememberNavController())
        }
    }
}