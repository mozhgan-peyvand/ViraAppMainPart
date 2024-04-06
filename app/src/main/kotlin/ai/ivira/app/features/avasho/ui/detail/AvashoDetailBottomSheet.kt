package ai.ivira.app.features.avasho.ui.detail

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.SnackBarWithPaddingBottom
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.avasho.ui.AvashoAnalytics
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.formatDuration
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersionBetween23And29
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.shareMp3
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_Card_Stroke
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_300
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_State_Layer_1
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.TextAutoSize
import ai.ivira.app.utils.ui.widgets.TextAutoSizeRange
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun AvashoDetailBottomSheet(
    animationProgress: Float,
    collapseToolbarAction: () -> Unit,
    halfToolbarAction: () -> Unit,
    changePlayingItemAction: (Boolean) -> Unit,
    avashoProcessedItem: AvashoProcessedFileView,
    isBottomSheetVisible: Boolean,
    isBottomSheetExpanded: Boolean,
    avashoDetailsViewModel: AvashoDetailsBottomSheetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playerState by avashoDetailsViewModel::playerState
    val verticalScroll = if (isBottomSheetVisible) Modifier.verticalScroll(rememberScrollState()) else Modifier
    val eventHandler = LocalEventHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val sheetState = rememberViraBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var selectedSheet by rememberSaveable { mutableStateOf(AvashoDetailsBottomSheetType.FileAccessPermissionDenied) }

    val writeStoragePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            avashoDetailsViewModel.saveToDownloadFolder(
                filePath = avashoProcessedItem.filePath,
                fileName = avashoProcessedItem.title
            ).also { isSuccess ->

                // fixme is it needed?
                sheetState.hide()

                if (isSuccess) {
                    showMessage(
                        snackbarHostState,
                        coroutineScope,
                        context.getString(R.string.msg_file_saved_successfully)
                    )
                }
            }
        } else {
            // fixme is it needed?
            sheetState.hide()

            avashoDetailsViewModel.putDeniedPermissionToSharedPref(
                permission = permission,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = permission
                )
            )
            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_file_permission)
            )
        }
    }

    LaunchedEffect(playerState.isPlaying) {
        changePlayingItemAction(playerState.isPlaying)
    }

    LaunchedEffect(isBottomSheetVisible) {
        val emitSuccess = playerState.tryInitWith(
            file = File(avashoProcessedItem.filePath),
            forcePrepare = true,
            autoStart = true
        )
        if (emitSuccess) {
            if (!isBottomSheetVisible) {
                playerState.stopMediaPlayer()
                playerState.reset()
            }
        }
    }

    LaunchedEffect(Unit) {
        avashoDetailsViewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                showMessage(
                    snackbarHostState,
                    coroutineScope,
                    it.message
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            playerState.stopMediaPlayer()
            playerState.reset()
            changePlayingItemAction(false)
        }
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        scaffoldState = scaffoldState,
        snackbarHost = snackBarHost@{ snackBarState ->
            if (!isBottomSheetExpanded) {
                SnackBarWithPaddingBottom(
                    snackbarHostState = snackBarState,
                    shouldShowOverItems = true,
                    paddingValue = 1200f
                )
                return@snackBarHost
            }

            SnackBar(
                snackbarHostState = snackBarState,
                paddingBottom = 32.dp
            )
        }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CollapseStateToolbar(
                    progress = animationProgress,
                    collapseToolbarAction = { collapseToolbarAction() },
                    halfToolbarAction = { halfToolbarAction() },
                    fileName = avashoProcessedItem.title
                )

                CollapseStatePlayer(
                    animationProgress = animationProgress,
                    playerState = playerState,
                    onProgressChanged = {
                        playerState.seekTo(it)
                    },
                    onPlayingChanged = {
                        if (!playerState.isPlaying) {
                            playerState.startPlaying()
                        } else {
                            playerState.stopPlaying()
                        }
                    },
                    onShareClick = {
                        eventHandler.specialEvent(AvashoAnalytics.shareItem)
                        shareMp3(
                            context = context,
                            file = File(avashoProcessedItem.filePath)
                        )
                    },
                    onSaveClicked = onClick@{
                        eventHandler.specialEvent(AvashoAnalytics.downloadItem)
                        if (!isSdkVersionBetween23And29()) {
                            avashoDetailsViewModel.saveToDownloadFolder(
                                filePath = avashoProcessedItem.filePath,
                                fileName = avashoProcessedItem.title
                            ).also { isSuccess ->
                                if (isSuccess) {
                                    showMessage(
                                        snackbarHostState,
                                        coroutineScope,
                                        context.getString(R.string.msg_file_saved_successfully)
                                    )
                                }
                            }
                            return@onClick
                        }

                        if (context.hasPermission(permission)) {
                            avashoDetailsViewModel.saveToDownloadFolder(
                                filePath = avashoProcessedItem.filePath,
                                fileName = avashoProcessedItem.title
                            ).also { isSuccess ->

                                if (isSuccess) {
                                    showMessage(
                                        snackbarHostState,
                                        coroutineScope,
                                        context.getString(R.string.msg_file_saved_successfully)
                                    )
                                }
                            }
                        } else if (avashoDetailsViewModel.hasDeniedPermissionPermanently(permission)) {
                            selectedSheet = AvashoDetailsBottomSheetType.FileAccessPermissionDenied
                            sheetState.show()
                        } else {
                            // Asking for permission
                            writeStoragePermission.launch(permission)
                        }
                    }
                )

                Divider(
                    color = Color_Card_Stroke,
                    modifier = Modifier.height(1.dp)
                )
                SelectionContainer {
                    Text(
                        text = avashoProcessedItem.text,
                        style = MaterialTheme.typography.body2,
                        color = Color_Text_2,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .then(verticalScroll)
                    )
                }
            }

            if (animationProgress < 1.0f && animationProgress > 0f) {
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) { },
                    content = {}
                )
            }
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(selectedSheet) {
                when (selectedSheet) {
                    AvashoDetailsBottomSheetType.FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(
                            cancelAction = {
                                sheetState.hide()
                            },
                            submitAction = {
                                navigateToAppSettings(activity = context as Activity)
                                sheetState.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollapseStateToolbar(
    progress: Float,
    modifier: Modifier = Modifier,
    collapseToolbarAction: () -> Unit,
    halfToolbarAction: () -> Unit,
    fileName: String
) {
    val size by remember(progress) {
        mutableStateOf(20.dp * (1 - progress))
    }

    val buttonSize by remember(progress) {
        mutableStateOf(48.dp * progress)
    }

    val tintColorClose by remember(progress) {
        mutableStateOf(Color_White.copy(1 - progress))
    }

    val tintColorMenu by remember(progress) {
        mutableStateOf(Color_White.copy(progress))
    }

    val h5 = MaterialTheme.typography.h5
    val s2 = MaterialTheme.typography.subtitle2

    val textStyle by remember(progress) {
        derivedStateOf {
            lerp(h5, s2, progress)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Divider(
            color = Color_State_Layer_1,
            modifier = Modifier
                .padding(top = 8.dp)
                .height(5.dp)
                .width(42.dp)
                .align(CenterHorizontally)
                .clip(shape = RoundedCornerShape(4.dp))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
        ) {
            IconButton(
                onClick = {
                    halfToolbarAction()
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(buttonSize)
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_arrow_down,
                    contentDescription = null,
                    tint = tintColorMenu
                )
            }

            Spacer(modifier = Modifier.size(size))

            Text(
                text = fileName,
                style = textStyle,
                color = Color_Text_1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            if (progress != 1f) {
                IconButton(
                    onClick = {
                        safeClick {
                            collapseToolbarAction()
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_close,
                        contentDescription = null,
                        tint = tintColorClose
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapseStatePlayer(
    animationProgress: Float,
    playerState: VoicePlayerState,
    onPlayingChanged: (Boolean) -> Unit,
    onProgressChanged: (Float) -> Unit,
    onSaveClicked: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration by remember(playerState.duration) {
        mutableFloatStateOf(playerState.duration.toFloat() / 1000)
    }

    val bottomBarHeight by remember(animationProgress) {
        mutableStateOf(104.dp * animationProgress)
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

        CompositionLocalProvider(LocalLayoutDirection provides Ltr) {
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
                        if (animationProgress == 1f) {
                            playerState.seekForward()
                            return@safeClick
                        }

                        onSaveClicked()
                    }
                }
            ) {
                ViraImage(
                    drawable = R.drawable.ic_download_voice,
                    contentDescription = stringResource(id = R.string.lbl_download),
                    alpha = 1 - animationProgress
                )

                ViraImage(
                    drawable = R.drawable.ic_ten_second_after,
                    contentDescription = stringResource(id = R.string.lbl_move_ten_sec_forward),
                    alpha = animationProgress
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
                        if (animationProgress == 1f) {
                            playerState.seekBackward()
                            return@safeClick
                        }

                        onShareClick()
                    }
                }
            ) {
                ViraImage(
                    drawable = R.drawable.ic_share_speech,
                    contentDescription = stringResource(id = R.string.lbl_share_file),
                    alpha = 1 - animationProgress
                )

                ViraImage(
                    drawable = R.drawable.ic_ten_second_before,
                    contentDescription = stringResource(id = R.string.lbl_move_ten_sec_back),
                    alpha = animationProgress
                )
            }
        }

        BottomBar(
            progress = animationProgress,
            onShareClick = {
                onShareClick()
            },
            onSaveClick = onSaveClicked,
            modifier = Modifier.height(bottomBarHeight)
        )
    }
}

@Composable
fun BottomBar(
    progress: Float,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val textTypography = MaterialTheme.typography.button
    val maxFontSize by remember(progress) {
        mutableStateOf(textTypography.fontSize * progress)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = modifier.fillMaxWidth()) {
            TextButton(
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 10.dp),
                onClick = {
                    safeClick {
                        onSaveClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color_Primary_300,
                    backgroundColor = Color_Primary_Opacity_15
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ViraImage(
                        drawable = R.drawable.ic_save,
                        contentDescription = stringResource(id = R.string.lbl_save),
                        modifier = modifier.padding(end = 10.dp)
                    )
                    TextAutoSize(
                        text = stringResource(id = R.string.lbl_save),
                        style = MaterialTheme.typography.button,
                        color = Color_Primary_300,
                        textScale = TextAutoSizeRange(
                            min = 10.sp,
                            max = maxFontSize
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            TextButton(
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 10.dp),
                onClick = {
                    safeClick {
                        onShareClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color_Primary_300,
                    backgroundColor = Color_Primary_Opacity_15
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
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
                        textScale = TextAutoSizeRange(
                            min = 10.sp,
                            max = maxFontSize
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@ViraDarkPreview
@Composable
private fun AvashoDetailBottomSheetPreview() {
    ViraPreview {
        AvashoDetailBottomSheet(
            animationProgress = 0f,
            collapseToolbarAction = {},
            halfToolbarAction = {},
            changePlayingItemAction = {},
            avashoProcessedItem = AvashoProcessedFileView(
                0,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0f,
                false,
                0,
                isSeen = false
            ),
            isBottomSheetVisible = false,
            isBottomSheetExpanded = false
        )
    }
}