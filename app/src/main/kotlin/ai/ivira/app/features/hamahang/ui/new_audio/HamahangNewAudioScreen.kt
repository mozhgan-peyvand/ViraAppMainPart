package ai.ivira.app.features.hamahang.ui.new_audio

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetState
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.ava_negar.ui.record.VoicePlayerState
import ai.ivira.app.features.hamahang.ui.new_audio.HamahangNewAudioResult.Companion.NEW_FILE_AUDIO_RESULT
import ai.ivira.app.features.hamahang.ui.new_audio.components.HamahangAudioBox
import ai.ivira.app.features.hamahang.ui.new_audio.components.HamahangAudioBoxMode
import ai.ivira.app.features.hamahang.ui.new_audio.sheets.HamahangNewAudioBottomSheetType
import ai.ivira.app.features.hamahang.ui.new_audio.sheets.HamahangNewAudioBottomSheetType.DeleteFileConfirmation
import ai.ivira.app.features.hamahang.ui.new_audio.sheets.HamahangNewAudioBottomSheetType.FileAccessPermissionDenied
import ai.ivira.app.features.hamahang.ui.new_audio.sheets.HamahangNewAudioBottomSheetType.UploadFile
import ai.ivira.app.features.hamahang.ui.new_audio.sheets.HamahangUploadFileBottomSheet
import ai.ivira.app.utils.ui.OnLifecycleEvent
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersion33orHigher
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.openAudioSelector
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Cyan_200
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HamahangNewAudioScreenRoute(navController: NavController) {
    HamahangNewAudioScreen(
        viewModel = hiltViewModel(),
        navigateUp = { result ->
            if (result != null) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        NEW_FILE_AUDIO_RESULT,
                        HamahangNewAudioResult(
                            inputPath = result.inputPath,
                            speaker = result.speaker
                        )
                    )
            }

            navController.popBackStack()
        }
    )
}

@Composable
private fun HamahangNewAudioScreen(
    viewModel: HamahangNewAudioViewModel,
    navigateUp: (HamahangNewAudioResult?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberViraBottomSheetState()
    var selectedSheet by rememberSaveable(
        stateSaver = HamahangNewAudioBottomSheetType.Saver()
    ) { mutableStateOf(UploadFile) }

    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val isOkToGenerate by viewModel.isOkToGenerate.collectAsStateWithLifecycle(false)
    val selectedSpeaker by viewModel.selectedSpeaker.collectAsStateWithLifecycle()
    val playerState by viewModel::playerState

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == ComponentActivity.RESULT_OK) {
            coroutineScope.launch {
                if (viewModel.checkIfUriDurationIsOk(context, it.data?.data)) {
                    kotlin.runCatching {
                        viewModel.setUploadedFile(uri = it.data?.data)
                    }
                }
            }
        }
    }

    val chooseAudioPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchOpenFile.launch(openAudioSelector())
        } else {
            val permission = if (isSdkVersion33orHigher()) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            viewModel.putDeniedPermissionToSharedPref(
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

    OnLifecycleEvent(
        onPause = {
            if (playerState.isPlaying) {
                playerState.stopPlaying()
            }
        }
    )

    LaunchedEffect(mode) {
        (mode as? HamahangAudioBoxMode.Preview)?.let { previewItem ->
            playerState.tryInitWith(
                file = previewItem.file,
                forcePrepare = true,
                autoStart = false
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                showMessage(snackbarHostState, this, it.message)
            }
        }
    }

    HamahangNewAudioUI(
        context = context,
        mode = mode,
        selectedSpeaker = selectedSpeaker,
        speakers = viewModel.speakers,
        scaffoldState = scaffoldState,
        scrollState = scrollState,
        isOkToGenerate = isOkToGenerate,
        onBackClick = { navigateUp(null) }, // TODO: add result as param
        onUploadFileClick = {
            when (mode) {
                HamahangAudioBoxMode.Idle -> selectedSheet = UploadFile
                is HamahangAudioBoxMode.Preview -> selectedSheet = DeleteFileConfirmation(fromUpload = true)
                HamahangAudioBoxMode.Recording -> {
                    // TODO: Add related action
                }
            }
            sheetState.show()
        },
        onGenerateClick = {
            if (playerState.isPlaying) {
                playerState.stopPlaying()
            }
        },
        changeSpeaker = { viewModel.changeSpeaker(it) },
        sheetState = sheetState,
        selectedSheet = selectedSheet,
        startRecording = viewModel::startRecording,
        stopRecording = viewModel::stopRecording,
        onDeleteClick = {
            selectedSheet = DeleteFileConfirmation(fromUpload = false)
            sheetState.show()
        },
        deleteFileAction = {
            when (mode) {
                HamahangAudioBoxMode.Idle -> sheetState.hide()
                is HamahangAudioBoxMode.Preview -> {
                    (selectedSheet as? DeleteFileConfirmation)?.let { previewMode ->
                        viewModel.deleteFile()
                        if (previewMode.fromUpload) {
                            selectedSheet = UploadFile
                            sheetState.show()
                        } else {
                            sheetState.hide()
                        }
                    }
                }
                HamahangAudioBoxMode.Recording -> {
                    // TODO: Add related action
                }
            }
        },
        playerState = playerState,
        openFiles = {
            sheetState.hide()

            // PermissionCheck Duplicate 1
            val permission = if (isSdkVersion33orHigher()) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (context.hasPermission(permission)) {
                launchOpenFile.launch(openAudioSelector())
            } else if (
                viewModel.hasDeniedPermissionPermanently(permission)
            ) {
                // needs improvement, just need to save if permission is alreadyRequested
                // and everytime check shouldShow
                selectedSheet = FileAccessPermissionDenied
                sheetState.show()
            } else {
                // Asking for permission
                chooseAudioPermLauncher.launch(permission)
            }
        }
    )
}

@Composable
private fun HamahangNewAudioUI(
    isOkToGenerate: Boolean,
    mode: HamahangAudioBoxMode,
    selectedSpeaker: HamahangSpeakerView?,
    speakers: List<HamahangSpeakerView>,
    selectedSheet: HamahangNewAudioBottomSheetType,
    playerState: VoicePlayerState,
    context: Context,
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    sheetState: ViraBottomSheetState,
    changeSpeaker: (HamahangSpeakerView) -> Unit,
    onDeleteClick: () -> Unit,
    deleteFileAction: () -> Unit,
    onBackClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onUploadFileClick: () -> Unit,
    openFiles: () -> Unit,
    startRecording: () -> Unit,
    stopRecording: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            HamahangNewAudioTopBar(
                onBackClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(state = scrollState)
            ) {
                InputAudioSection(
                    mode = mode,
                    playerState = playerState,
                    onDeleteClick = onDeleteClick,
                    onUploadFileClick = onUploadFileClick,
                    startRecording = startRecording,
                    stopRecording = stopRecording,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                SelectSpeakerSection(
                    speakers = speakers,
                    selectedSpeaker = selectedSpeaker,
                    changeSpeaker = changeSpeaker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            ConfirmButton(
                onClick = onGenerateClick,
                enabled = isOkToGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp)
            )
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(selectedSheet) sheetContent@{ selected ->
                when (selected) {
                    UploadFile -> {
                        HamahangUploadFileBottomSheet(
                            submitAction = openFiles
                        )
                    }
                    FileAccessPermissionDenied -> {
                        AccessDeniedToOpenFileBottomSheet(
                            cancelAction = { sheetState.hide() },
                            submitAction = {
                                navigateToAppSettings(activity = context as Activity)
                                sheetState.hide()
                            }
                        )
                    }
                    is DeleteFileConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = deleteFileAction,
                            cancelAction = { sheetState.hide() },
                            fileName = ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputAudioSection(
    mode: HamahangAudioBoxMode,
    playerState: VoicePlayerState,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    onDeleteClick: () -> Unit,
    onUploadFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.lbl_input_audio),
                style = MaterialTheme.typography.subtitle2,
                color = LocalContentColor.current
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            UploadFileButton(onUploadFileClick = onUploadFileClick)
        }

        Text(
            text = stringResource(id = R.string.msg_input_audio_desc),
            style = MaterialTheme.typography.body1.copy(
                color = Color_Text_3
            ),
            modifier = Modifier.fillMaxWidth()
        )

        HamahangAudioBox(
            mode = mode,
            stopRecording = stopRecording,
            startRecording = startRecording,
            playerState = playerState,
            onDeleteClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun UploadFileButton(
    onUploadFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color_Primary_Opacity_15)
            .clickable {
                safeClick(event = onUploadFileClick)
            }
            .padding(vertical = 8.dp)
            .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ViraIcon(
            drawable = R.drawable.ic_upload,
            contentDescription = stringResource(id = R.string.lbl_select),
            tint = Cyan_200
        )
        Text(
            text = stringResource(id = R.string.lbl_upload),
            style = MaterialTheme.typography.overline.copy(color = Cyan_200)
        )
    }
}

@Composable
private fun SelectSpeakerSection(
    speakers: List<HamahangSpeakerView>,
    selectedSpeaker: HamahangSpeakerView?,
    changeSpeaker: (HamahangSpeakerView) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.lbl_select_speaker),
            style = MaterialTheme.typography.subtitle2,
            color = LocalContentColor.current
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.msg_select_speaker_desc),
            style = MaterialTheme.typography.body1.copy(
                color = Color_Text_3
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SpeakerSelection(
            speakers = speakers,
            selectedSpeaker = selectedSpeaker,
            changeSpeaker = changeSpeaker,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

private const val SPEAKER_ITEMS_IN_ROW = 3

@Composable
private fun SpeakerSelection(
    speakers: List<HamahangSpeakerView>,
    selectedSpeaker: HamahangSpeakerView?,
    changeSpeaker: (HamahangSpeakerView) -> Unit,
    modifier: Modifier = Modifier,
    itemsInRow: Int = SPEAKER_ITEMS_IN_ROW
) {
    BoxWithConstraints(modifier = modifier) {
        val paddingBetweenItems = remember { 8 } // will be converted to dp
        val horizontalPaddingsBetweenItems = remember { ((itemsInRow - 1) * paddingBetweenItems).dp }
        val itemWidth = remember(maxWidth, horizontalPaddingsBetweenItems) {
            (maxWidth - horizontalPaddingsBetweenItems) / itemsInRow
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(paddingBetweenItems.dp),
            verticalArrangement = Arrangement.spacedBy(paddingBetweenItems.dp),
            maxItemsInEachRow = itemsInRow
        ) {
            speakers.forEach {
                SpeakerItem(
                    item = it,
                    isSelected = selectedSpeaker == it,
                    onItemClick = { changeSpeaker(it) },
                    modifier = Modifier.width(itemWidth)
                )
            }
        }
    }
}

@Composable
private fun SpeakerItem(
    item: HamahangSpeakerView,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = remember { RoundedCornerShape(8.dp) }
    val titleOverFlow by remember(isSelected) {
        mutableStateOf(if (isSelected) TextOverflow.Clip else TextOverflow.Ellipsis)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(
                color = if (isSelected) Color_Primary_Opacity_15 else Color_Surface_Container_High,
                shape = shape
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        border = BorderStroke(width = 1.dp, color = Color_Primary),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .clickable { onItemClick() }
            .padding(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            ViraImage(
                drawable = item.iconRes,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp))
            )
        }

        Text(
            text = item.viewName,
            style = MaterialTheme.typography.body2,
            color = if (isSelected) Color_Primary else Color_Text_2,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .then(
                    if (isSelected) Modifier.basicMarquee() else Modifier
                ),
            textAlign = TextAlign.Center,
            overflow = titleOverFlow
        )
    }
}

@Composable
private fun HamahangNewAudioTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = {
                safeClick {
                    onBackClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                modifier = Modifier.padding(12.dp),
                contentDescription = stringResource(id = R.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_hamahang),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Button(
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = {
            safeClick(event = onClick)
        },
        colors = ButtonDefaults.buttonColors(
            disabledBackgroundColor = if (!enabled) {
                MaterialTheme.colors.onSurface
                    .copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface)
            } else {
                Color_Primary
            }
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.lbl_generate_audio),
            style = MaterialTheme.typography.button,
            color = Color_Text_1,
            modifier = Modifier.onGloballyPositioned {
                lottieHeight = it.size.height
            }
        )
    }
}

// FIXME: Has problem rendering preview
@RequiresApi(34)
@Preview
@Composable
private fun PreviewHamahangNewAudioUI() {
    ViraPreview {
        val context = LocalContext.current
        val fakePlayerState = VoicePlayerState(
            MediaPlayer(context),
            context.applicationContext as Application
        )
        HamahangNewAudioUI(
            mode = HamahangAudioBoxMode.Idle,
            selectedSpeaker = null,
            isOkToGenerate = false,
            scaffoldState = rememberScaffoldState(),
            scrollState = rememberScrollState(),
            changeSpeaker = {},
            onBackClick = {},
            onGenerateClick = {},
            onUploadFileClick = {},
            sheetState = rememberViraBottomSheetState(),
            selectedSheet = UploadFile,
            startRecording = {},
            stopRecording = {},
            openFiles = {},
            speakers = HamahangSpeakerView.values().asList(),
            playerState = fakePlayerState,
            onDeleteClick = {},
            deleteFileAction = {},
            context = LocalContext.current
        )
    }
}