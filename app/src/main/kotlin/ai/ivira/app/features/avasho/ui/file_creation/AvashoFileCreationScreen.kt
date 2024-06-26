package ai.ivira.app.features.avasho.ui.file_creation

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.ChooseFileContentBottomSheet
import ai.ivira.app.features.avasho.ui.AvashoAnalytics
import ai.ivira.app.features.avasho.ui.file_creation.FileCreationBottomSheetType.ChooseFile
import ai.ivira.app.features.avasho.ui.file_creation.FileCreationBottomSheetType.FileAccessPermissionDenied
import ai.ivira.app.features.avasho.ui.file_creation.FileCreationBottomSheetType.OpenForChooseSpeaker
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.ViraBalloon
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.openFileIntent
import ai.ivira.app.utils.ui.openFileIntentAndroidTiramisu
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.skydoves.balloon.overlay.BalloonOverlayCircle
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val CHAR_COUNT = 2500

@Composable
fun AvashoFileCreationScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(AvashoAnalytics.screenViewFileCreation)
    }

    AvashoFileCreationScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AvashoFileCreationScreen(
    navController: NavHostController,
    viewModel: AvashoFileCreationViewModel
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val uiViewState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)

    val launchOpenFile = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            val data = result.data
            val uri = data?.data
            if (uri != null) {
                coroutineScope.launch(IO) {
                    runCatching {
                        // The reason this is added because **use** function closes inputStream!
                        // noinspection Recycle
                        val inputStream = context.contentResolver.openInputStream(uri)
                        viewModel.appendToText(
                            inputStream?.bufferedReader()?.use {
                                val text = it.readText()
                                if (text.length > CHAR_COUNT) {
                                    text.substring(0, CHAR_COUNT)
                                } else {
                                    text
                                }
                            }.orEmpty()
                        )
                    }
                }
            }
        }
    }

    val chooseReadTextPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchOpenFile.launch(
                openFileIntent(
                    type = "text/*",
                    mimeType = "text/plain"
                )
            )
        } else {
            if (Build.VERSION.SDK_INT >= 33) {
                launchOpenFile.launch(
                    openFileIntentAndroidTiramisu()
                )
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            viewModel.putDeniedPermissionToSharedPref(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                deniedPermanently = isPermissionDeniedPermanently(
                    activity = context as Activity,
                    permission = Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.lbl_need_to_access_file_permission)
            )
        }
    }

    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(
            FileAccessPermissionDenied
        )
    }

    val shouldShowTooltip by viewModel.shouldShowTooltip

    val fileName = rememberSaveable { mutableStateOf(viewModel.getCurrentDefaultName()) }
    val sheetState = rememberViraBottomSheetState()

    val eventHandler = LocalEventHandler.current

    LaunchedEffect(focusRequester, shouldShowTooltip) {
        if (shouldShowTooltip) return@LaunchedEffect

        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                showMessage(snackbarHostState, this, it.message)
            }
        }
    }

    Scaffold(
        backgroundColor = Color_BG,
        scaffoldState = scaffoldState,
        snackbarHost = { snackBarHostState ->
            SnackBar(
                snackbarHostState = snackBarHostState,
                paddingBottom = 80.dp,
                maxLine = 2
            )
        }
    ) { padding ->
        var shouldShowUploadTooltip by rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color_BG)
                .padding(padding)
        ) {
            TopAppBar(
                isUndoEnabled = viewModel.canUndo(),
                isRedoEnabled = viewModel.canRedo(),
                shouldShowUploadTooltip = shouldShowUploadTooltip,
                onUndoClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.undo()
                },
                onRedoClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.redo()
                },
                onBackAction = {
                    // todo should handle the situation when the textField is not empty
                    navController.navigateUp()
                },
                uploadAction = {
                    eventHandler.specialEvent(AvashoAnalytics.uploadIconClick)
                    focusManager.clearFocus()
                    setSelectedSheet(ChooseFile)
                    sheetState.show()
                },
                onTooltipDismiss = {
                    viewModel.doNotShowTooltipAgain()
                    shouldShowUploadTooltip = false
                    focusRequester.requestFocus()
                }
            )

            Body(
                text = viewModel.textBody.value,
                focusRequester = focusRequester,
                shouldShowEditTextTooltip = shouldShowTooltip && !shouldShowUploadTooltip,
                onTooltipDismiss = {
                    if (shouldShowTooltip) {
                        shouldShowUploadTooltip = true
                    }
                },
                onTextChange = {
                    viewModel.addTextToList(it)
                },
                modifier = Modifier.weight(1f)
            )

            ConfirmButton(
                enabled = viewModel.textBody.value.isNotEmpty() && uiViewState !is UiLoading,
                isLoading = uiViewState is UiLoading,
                onClick = {
                    keyboardController?.hide()
                    viewModel.checkSpeech(speech = viewModel.textBody.value) {
                        setSelectedSheet(OpenForChooseSpeaker)
                        sheetState.show()
                    }
                }
            )
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(selectedSheet) { selectedSheet ->
                when (selectedSheet) {
                    OpenForChooseSpeaker -> {
                        SelectSpeakerBottomSheet(
                            fileName = fileName.value,
                            uploadFileAction = { nameFile, selectedItem ->
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(
                                        SpeechResult.FILE_NAME,
                                        SpeechResult(
                                            fileName = nameFile,
                                            text = viewModel.textBody.value,
                                            speakerType = selectedItem.value
                                        )
                                    )
                                if (nameFile == viewModel.getCurrentDefaultName()) {
                                    viewModel.updateCurrentDefaultName()
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                    FileAccessPermissionDenied -> {
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
                    ChooseFile -> {
                        ChooseFileContentBottomSheet(
                            onOpenFile = {
                                sheetState.hide()

                                if (Build.VERSION.SDK_INT >= 33) {
                                    launchOpenFile.launch(
                                        openFileIntentAndroidTiramisu()
                                    )
                                } else {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        launchOpenFile.launch(
                                            openFileIntent(
                                                type = "text/*",
                                                mimeType = "text/plain"
                                            )
                                        )
                                    } else if (viewModel.hasDeniedPermissionPermanently(
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        )
                                    ) {
                                        setSelectedSheet(FileAccessPermissionDenied)
                                        sheetState.show()
                                    } else {
                                        // Asking for permission
                                        chooseReadTextPermLauncher.launch(
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        )
                                    }
                                }
                            },
                            descriptionFileFormat = R.string.lbl_lbl_allow_text_format
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
    shouldShowUploadTooltip: Boolean,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onBackAction: () -> Unit,
    uploadAction: () -> Unit,
    onTooltipDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                safeClick {
                    onBackAction()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                modifier = Modifier.padding(8.dp),
                contentDescription = stringResource(id = R.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_file_creation),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.subtitle2,
            color = Color_White
        )

        ViraBalloon(
            text = stringResource(id = R.string.tt_can_select_file_from_here),
            marginVertical = 16,
            overLayShape = BalloonOverlayCircle(with(density) { 30.dp.roundToPx().toFloat() }),
            onDismiss = { onTooltipDismiss() }
        ) {
            IconButton(
                onClick = {
                    safeClick {
                        uploadAction()
                    }
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_upload_txt_file,
                    contentDescription = stringResource(id = R.string.desc_upload)
                )
            }

            if (shouldShowUploadTooltip) {
                showAlignBottom()
            }
        }

        IconButton(
            enabled = isRedoEnabled,
            onClick = {
                safeClick {
                    onRedoClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_redo,
                contentDescription = stringResource(id = R.string.desc_redo),
                modifier = Modifier.padding(12.dp)
            )
        }

        IconButton(
            enabled = isUndoEnabled,
            onClick = {
                safeClick { onUndoClick() }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_undo,
                contentDescription = stringResource(id = R.string.desc_undo),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun Body(
    text: String,
    focusRequester: FocusRequester,
    shouldShowEditTextTooltip: Boolean,
    onTextChange: (String) -> Unit,
    onTooltipDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                safeClick {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = text,
                textStyle = MaterialTheme.typography.body1,
                placeholder = {
                    ViraBalloon(
                        text = stringResource(id = R.string.tt_can_type_text_here),
                        marginHorizontal = 26,
                        marginVertical = 8,
                        onDismiss = { onTooltipDismiss() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.lbl_type_text_or_import_file),
                            style = MaterialTheme.typography.body1,
                            color = Color_Text_3,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        LaunchedEffect(shouldShowEditTextTooltip) {
                            if (shouldShowEditTextTooltip) {
                                coroutineScope.launch {
                                    // show tooltip with delay to let the tooltip finds its exact overlay position
                                    delay(200)
                                    showAlignBottom()
                                }
                            }
                        }
                    }
                },
                onValueChange = {
                    val generatedText = if (it.length <= CHAR_COUNT) {
                        it
                    } else {
                        it.substring(0 ..< CHAR_COUNT)
                    }

                    onTextChange(generatedText)
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.size(52.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = Color_Surface_Container_High,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .pointerInput(Unit) {}
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelMedium) {
                Text(
                    text = stringResource(id = R.string.lbl_character),
                    color = Color_Primary_200
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = buildString {
                        append(text.length)
                        append("/")
                        append(CHAR_COUNT)
                    },
                    color = Color_Primary_200
                )
            }
        }
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Button(
        contentPadding = PaddingValues(vertical = 12.dp),
        onClick = {
            safeClick(event = onClick)
        },
        colors = ButtonDefaults.buttonColors(
            disabledBackgroundColor = if (!enabled && !isLoading) {
                MaterialTheme.colors.onSurface
                    .copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface)
            } else {
                Color_Primary
            }
        ),
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (isLoading) {
            LoadingLottie(
                modifier = Modifier.height(with(density) { lottieHeight.toDp() })
            )
        } else {
            Text(
                text = stringResource(id = R.string.lbl_convert_to_sound),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                modifier = Modifier.onGloballyPositioned {
                    lottieHeight = it.size.height
                }
            )
        }
    }
}

@Composable
private fun LoadingLottie(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(resId = R.raw.lottie_loading_2)
    )
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun AvashoFileCreationPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            AvashoFileCreationScreen(
                navController = rememberNavController(),
                viewModel = hiltViewModel()
            )
        }
    }
}