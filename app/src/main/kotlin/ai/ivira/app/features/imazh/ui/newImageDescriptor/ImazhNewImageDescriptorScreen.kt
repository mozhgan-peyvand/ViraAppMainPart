package ai.ivira.app.features.imazh.ui.newImageDescriptor

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.ui.ImazhAnalytics
import ai.ivira.app.features.imazh.ui.newImageDescriptor.NewImageDescriptorViewModel.Companion.PROMPT_CHARACTER_LIMIT
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhKeywordItem
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhStyleItem
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordChipType
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhBackConfirmationWhileEditingBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhBackConfirmationWhileGeneratingBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhHistoryBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhKeywordBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.BackWhileEditing
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.BackWhileGenerate
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.History
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.Keywords
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.RandomPrompt
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.Style
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhRandomConfirmationBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhSelectStyleBottomSheet
import ai.ivira.app.utils.ui.TooltipHelper
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.ViraBalloon
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Border
import ai.ivira.app.utils.ui.theme.Color_Info_700
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_40
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Cyan_200
import ai.ivira.app.utils.ui.widgets.ViraIcon
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val EXPANSION_VISIBILITY_DELAY = 100

@Composable
fun ImazhNewImageDescriptorScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(ImazhAnalytics.screenViewNewImageDescriptor)
    }

    ImazhNewImageDescriptorScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun ImazhNewImageDescriptorScreen(
    navController: NavHostController,
    viewModel: NewImageDescriptorViewModel
) {
    val uiState by viewModel.uiViewState.collectAsStateWithLifecycle()
    val isLoading by remember(uiState) { derivedStateOf { uiState is UiLoading } }
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState: ScrollState = rememberScrollState()
    val imazhKeywords by viewModel.imazhKeywords.collectAsStateWithLifecycle()
    var isErrorSnackBar by remember { mutableStateOf(false) }
    val promptIsValid by viewModel.promptIsValid
    val isOkToGenerate by remember {
        derivedStateOf { viewModel.prompt.value.isNotBlank() && !isLoading && promptIsValid }
    }
    val view = LocalView.current
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val isHistoryButtonVisible by remember(viewModel.historyList.value) {
        mutableStateOf(viewModel.historyList.value.isNotEmpty())
    }
    val availableStyles by viewModel.availableStyles.collectAsState(initial = emptyList())
    val tooltipHelper = remember {
        TooltipHelper(
            scrollToPosition = { coroutineScope.launch { scrollState.scrollTo(it) } },
            onAllTooltipsShown = { viewModel.doNotShowFirstRunAgain() }
        )
    }

    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(History)
    }

    val sheetState = rememberViraBottomSheetState()

    val actionBack: () -> Unit = remember {
        {
            viewModel.handelBackButton(
                navigateUp = {
                    navController.navigateUp()
                },
                backWhileEditing = {
                    setSelectedSheet(BackWhileEditing)
                    sheetState.show()
                },
                backWhileGenerating = {
                    setSelectedSheet(BackWhileGenerate)
                    sheetState.show()
                }
            )
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val promptPosition = remember { mutableIntStateOf(0) }
    val randomPromptPosition = remember { mutableIntStateOf(0) }
    val keywordsPosition = remember { mutableIntStateOf(0) }
    val topBarPosition = remember { mutableIntStateOf(0) }

    LaunchedEffect(tooltipHelper.isTooltipRunning.value) {
        if (tooltipHelper.isTooltipRunning.value) {
            keyboardController?.hide()
        }
    }

    LaunchedEffect(viewModel.shouldShowFirstRun.value) {
        delay(200) // To ensure positions are calculated and avoid mis-positioning of first balloon item in first-run
        if (viewModel.shouldShowFirstRun.value) {
            tooltipHelper.setupTooltipChainRunner(
                listOf(
                    Pair(
                        ImazhTooltip.Prompt,
                        promptPosition.intValue - topBarPosition.intValue
                    ),
                    Pair(
                        ImazhTooltip.RandomPrompt,
                        randomPromptPosition.intValue - topBarPosition.intValue
                    )
                )
            )
        }
    }

    LaunchedEffect(promptIsValid) {
        if (!promptIsValid) {
            isErrorSnackBar = true
            showMessage(
                snackbarHostState,
                coroutineScope,
                context.getString(R.string.msg_your_text_contains_inappropriate_words)
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
            delay(100) // To avoid seeing color changing of snackbar
            isErrorSnackBar = false
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) {
            focusManager.clearFocus()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    BackHandler(!sheetState.showBottomSheet) {
        actionBack()
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
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiError -> {
                val message = (uiState as? UiError)?.message
                    ?: context.getString(R.string.msg_there_is_a_problem)
                showMessage(
                    snackbarHostState,
                    coroutineScope,
                    message
                )

                // fixme should remove it, replace stateFlow with sharedFlow in viewModel
                viewModel.clearUiState()
            }

            is UiLoading -> {}
            is UiSuccess -> {
                // FIXME: Fix this. It's not Jaaleb!
                if (promptIsValid) {
                    navController.navigateUp()
                    viewModel.clearUiState()
                }
            }
            UiIdle -> {}
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        snackbarHost = {
            SnackBar(
                snackbarHostState = it,
                paddingBottom = 96.dp,
                maxLine = 2,
                isError = isErrorSnackBar
            )
        },
        topBar = {
            NewImageDescriptorTopBar(
                onBackClick = {
                    actionBack()
                },
                modifier = Modifier.onGloballyPositioned {
                    topBarPosition.intValue = it.size.height
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) { paddingValues ->
        Column(Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(state = scrollState)
                    .padding(paddingValues = paddingValues)
            ) {
                Prompt(
                    prompt = viewModel.prompt.value,
                    promptIsValid = promptIsValid,
                    isHistoryButtonVisible = isHistoryButtonVisible,
                    onPromptChange = viewModel::changePrompt,
                    resetPrompt = viewModel::resetPrompt,
                    onHistoryClick = {
                        setSelectedSheet(History)
                        sheetState.show()
                    },
                    onRandomClick = {
                        viewModel.generateRandomPrompt(
                            confirmationCallback = {
                                setSelectedSheet(RandomPrompt)
                                sheetState.show()
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .onGloballyPositioned {
                            promptPosition.intValue = it.positionInRoot().y.roundToInt()
                        },
                    tooltipHelper = tooltipHelper,
                    onRandomPromptPositionChange = { randomPromptPosition.intValue = it }
                )

                Keywords(
                    keywords = viewModel.selectedKeywords.value.toList(),
                    onAddKeywordClick = {
                        setSelectedSheet(Keywords)
                        sheetState.show()
                    },
                    onChipClick = { item ->
                        viewModel.removeKeyword(item)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .onGloballyPositioned {
                            keywordsPosition.intValue = it.positionInRoot().y.roundToInt()
                        },
                    tooltipHelper = tooltipHelper
                )

                Style(
                    selectedStyle = viewModel.selectedStyle.value,
                    isExpandable = viewModel.selectedStyle.value != ImazhImageStyle.None,
                    openStyleBottomSheet = {
                        setSelectedSheet(Style)
                        sheetState.show()
                    },
                    selectStyleCallBack = {
                        viewModel.selectStyle(it)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                NegativePrompt(
                    negativePrompt = viewModel.negativePrompt.value,
                    onNegativePromptChange = viewModel::setNegativePrompt,
                    resetNegativePrompt = viewModel::resetNegativePrompt,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    tooltipHelper = tooltipHelper
                )
            }

            if (!isKeyboardVisible) {
                ConfirmButton(
                    onClick = viewModel::generateImage,
                    enabled = isOkToGenerate,
                    isLoading = isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(selectedSheet) { selected ->
                when (selected) {
                    History -> {
                        ImazhHistoryBottomSheet(
                            onAcceptClick = { prompt ->
                                viewModel.changePrompt(prompt)
                                sheetState.hide()
                            }
                        )
                    }
                    RandomPrompt -> {
                        ImazhRandomConfirmationBottomSheet(
                            cancelAction = {
                                sheetState.hide()
                            },
                            deleteAction = {
                                viewModel.resetPrompt()
                                viewModel.generateRandomPrompt(
                                    confirmationCallback = { }
                                )
                                sheetState.hide()
                            }
                        )
                    }
                    Style -> {
                        ImazhSelectStyleBottomSheet(
                            styles = availableStyles,
                            selectedStyle = viewModel.selectedStyle.value,
                            confirmSelectionCallBack = {
                                viewModel.selectStyle(it)
                                sheetState.hide()
                            }
                        )
                    }
                    Keywords -> {
                        ImazhKeywordBottomSheet(
                            map = imazhKeywords,
                            selectedChips = viewModel.selectedKeywords.value,
                            onAcceptClick = { list ->
                                viewModel.updateKeywordList(list)
                                sheetState.hide()
                            }
                        )
                    }
                    BackWhileGenerate -> {
                        ImazhBackConfirmationWhileGeneratingBottomSheet(
                            continueAction = {
                                sheetState.hide()
                            },
                            deleteAction = {
                                viewModel.cancelConvertTextToImageRequest()
                                sheetState.hide()
                                navController.navigateUp()
                            }
                        )
                    }
                    BackWhileEditing -> {
                        ImazhBackConfirmationWhileEditingBottomSheet(
                            generateAction = {
                                sheetState.hide()
                                if (viewModel.prompt.value.isNotBlank()) {
                                    viewModel.generateImage()
                                }
                            },
                            deleteAction = {
                                sheetState.hide()
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewImageDescriptorTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = {
                safeClick(event = onBackClick)
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
            text = stringResource(id = R.string.lbl_imazh),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

// Duplicate 1
@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var lottieHeight by rememberSaveable { mutableIntStateOf(0) }

    Button(
        contentPadding = PaddingValues(vertical = 14.dp),
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
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        if (isLoading) {
            LoadingLottie(
                modifier = Modifier.height(with(density) { lottieHeight.toDp() })
            )
        } else {
            Text(
                text = stringResource(id = R.string.lbl_generate_image),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                modifier = Modifier.onGloballyPositioned {
                    lottieHeight = it.size.height
                }
            )
        }
    }
}

// Duplicate 1
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

@Composable
private fun AnimatableContent(
    isVisible: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val enterAnimation = remember {
        expandVertically(
            animationSpec = tween(EXPANSION_VISIBILITY_DELAY),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(EXPANSION_VISIBILITY_DELAY)
        )
    }
    val exitAnimation = remember {
        shrinkVertically(
            animationSpec = tween(EXPANSION_VISIBILITY_DELAY),
            shrinkTowards = Alignment.Top
        ) + fadeOut(
            animationSpec = tween(EXPANSION_VISIBILITY_DELAY)
        )
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = enterAnimation,
        exit = exitAnimation,
        label = "animated content",
        content = { content() }
    )
}

@Composable
private fun Prompt(
    prompt: String,
    promptIsValid: Boolean,
    isHistoryButtonVisible: Boolean,
    onPromptChange: (String) -> Unit,
    resetPrompt: () -> Unit,
    onHistoryClick: () -> Unit,
    onRandomClick: () -> Unit,
    onRandomPromptPositionChange: (Int) -> Unit,
    tooltipHelper: TooltipHelper,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header(
            title = R.string.lbl_image_prompt,
            hasInfo = true,
            setupInfoTooltip = {
                coroutineScope.launch {
                    tooltipHelper.setupSingleTooltipRunner(ImazhTooltip.Prompt, null)
                }
            },
            onTooltipDismiss = {
                coroutineScope.launch { tooltipHelper.next() }
            },
            showInfoTooltip = tooltipHelper.getTooltipStateByKey(ImazhTooltip.Prompt)?.value
                ?: false,
            infoTooltipMessage = R.string.msg_firs_run_image_description
        )
        PromptInputText(
            prompt = prompt,
            promptIsValid = promptIsValid,
            isHistoryButtonVisible = isHistoryButtonVisible,
            onPromptChange = onPromptChange,
            resetPrompt = resetPrompt,
            charLimit = PROMPT_CHARACTER_LIMIT,
            onHistoryClick = onHistoryClick,
            onRandomClick = onRandomClick,
            tooltipHelper = tooltipHelper,
            onRandomPromptPositionChange = onRandomPromptPositionChange
        )
    }
}

@Composable
private fun PromptInputText(
    prompt: String,
    promptIsValid: Boolean,
    isHistoryButtonVisible: Boolean,
    onPromptChange: (String) -> Unit,
    resetPrompt: () -> Unit,
    onRandomClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onRandomPromptPositionChange: (Int) -> Unit,
    tooltipHelper: TooltipHelper,
    charLimit: Int? = null
) {
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (promptIsValid) Color_Border else Color_Red,
                shape = RoundedCornerShape(4.dp)
            )
            .onGloballyPositioned {
                onRandomPromptPositionChange(it.positionInRoot().y.toInt())
            }
    ) {
        InputTextSection(
            text = prompt,
            placeHolder = stringResource(id = R.string.msg_describe_your_image),
            focusRequester = focusRequester,
            onTextChange = onPromptChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClearTextIcon(
                enabled = prompt.isNotBlank(),
                clear = resetPrompt
            )

            charLimit?.let {
                Text(
                    text = buildString {
                        append(prompt.length)
                        append("/")
                        append(it.toString())
                    },
                    style = MaterialTheme.typography.caption
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (isHistoryButtonVisible) {
                IconButton(
                    onClick = {
                        safeClick(event = onHistoryClick)
                    }
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_history,
                        contentDescription = stringResource(id = R.string.lbl_history),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            ViraBalloon(
                text = stringResource(id = R.string.msg_firs_run_random_prompt),
                marginVertical = 0,
                marginHorizontal = 16,
                onDismiss = {
                    coroutineScope.launch { tooltipHelper.next() }
                }
            ) {
                IconButton(
                    onClick = {
                        safeClick(event = onRandomClick)
                    }
                ) {
                    ViraIcon(
                        drawable = R.drawable.ic_random,
                        contentDescription = stringResource(id = R.string.lbl_random),
                        tint = MaterialTheme.colors.primary
                    )
                }
                if (tooltipHelper.getTooltipStateByKey(ImazhTooltip.RandomPrompt)?.value == true) {
                    showAlignBottom()
                }
            }
        }
    }
}

@Composable
private fun Keywords(
    keywords: List<ImazhKeywordView>,
    onAddKeywordClick: () -> Unit,
    onChipClick: (String) -> Unit,
    tooltipHelper: TooltipHelper,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var expandState by rememberSaveable(keywords) {
        mutableStateOf(
            if (keywords.isEmpty()) {
                ExpandState.Collapsed
            } else {
                ExpandState.Expanded
            }
        )
    }
    val coroutineScope = rememberCoroutineScope()

    Header(
        title = R.string.lbl_image_attributes,
        hasExpandButton = true,
        expandState = expandState,
        expandable = keywords.isNotEmpty(),
        onExpandClick = { expandState = expandState.toggleState() },
        sectionActionButton = {
            SectionActionButton(
                stringRes = R.string.lbl_add,
                iconRes = R.drawable.ic_add_small,
                action = {
                    onAddKeywordClick()
                }
            )
        },
        modifier = modifier,
        hasInfo = true,
        setupInfoTooltip = {
            coroutineScope.launch {
                tooltipHelper.setupSingleTooltipRunner(ImazhTooltip.Keywords, null)
            }
        },
        onTooltipDismiss = {
            coroutineScope.launch { tooltipHelper.next() }
        },
        showInfoTooltip = tooltipHelper.getTooltipStateByKey(ImazhTooltip.Keywords)?.value
            ?: false,
        infoTooltipMessage = R.string.msg_firs_run_keywords
    )

    AnimatableContent(
        isVisible = expandState == ExpandState.Expanded,
        content = {
            Row {
                LazyRow(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        key = { keywordView -> keywordView.keywordName },
                        items = keywords
                    ) { list ->
                        ImazhKeywordItem(
                            value = list,
                            type = ImazhKeywordChipType.SelectedWithNormalBackground,
                            onClick = { onChipClick(list.keywordName) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Style(
    selectedStyle: ImazhImageStyle,
    isExpandable: Boolean,
    selectStyleCallBack: (ImazhImageStyle) -> Unit,
    openStyleBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandState by rememberSaveable { mutableStateOf(ExpandState.Collapsed) }

    LaunchedEffect(selectedStyle) {
        expandState = if (selectedStyle == ImazhImageStyle.None) {
            ExpandState.Collapsed
        } else {
            ExpandState.Expanded
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Header(
            title = R.string.lbl_image_style,
            hasExpandButton = true,
            expandState = expandState,
            expandable = isExpandable,
            onExpandClick = { expandState = expandState.toggleState() },
            sectionActionButton = {
                SectionActionButton(
                    stringRes = if (selectedStyle != ImazhImageStyle.None) R.string.lbl_change else R.string.lbl_select,
                    iconRes = if (selectedStyle != ImazhImageStyle.None) R.drawable.ic_edit else R.drawable.ic_add_small,
                    action = openStyleBottomSheet
                )
            },
            hasInfo = false,
            setupInfoTooltip = {},
            onTooltipDismiss = {},
            showInfoTooltip = false
        )

        AnimatableContent(
            isVisible = expandState == ExpandState.Expanded && selectedStyle != ImazhImageStyle.None,
            content = {
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    IconButton(
                        onClick = { safeClick { selectStyleCallBack(ImazhImageStyle.None) } },
                        modifier = Modifier
                            .padding(2.dp)
                            .zIndex(1f)
                    ) {
                        ViraIcon(
                            drawable = R.drawable.ic_close_circle,
                            contentDescription = stringResource(id = R.string.lbl_btn_delete),
                            tint = Color.Unspecified
                        )
                    }
                    ImazhStyleItem(
                        style = selectedStyle,
                        isSelected = true,
                        showBorderOnSelection = false,
                        onItemClick = null
                    )
                }
            }
        )
    }
}

@Composable
private fun NegativePrompt(
    negativePrompt: String,
    onNegativePromptChange: (String) -> Unit,
    resetNegativePrompt: () -> Unit,
    tooltipHelper: TooltipHelper,
    modifier: Modifier = Modifier
) {
    var expandState by rememberSaveable { mutableStateOf(ExpandState.Collapsed) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header(
            title = R.string.lbl_negative_prompt,
            hasExpandButton = true,
            expandState = expandState,
            expandable = true,
            onExpandClick = { expandState = it.toggleState() },
            hasInfo = true,
            setupInfoTooltip = {
                coroutineScope.launch {
                    tooltipHelper.setupSingleTooltipRunner(ImazhTooltip.NegativePrompt, null)
                }
            },
            onTooltipDismiss = { coroutineScope.launch { tooltipHelper.next() } },
            showInfoTooltip = tooltipHelper.getTooltipStateByKey(ImazhTooltip.NegativePrompt)?.value
                ?: false,
            infoTooltipMessage = R.string.msg_firs_run_negative_prompt
        )

        AnimatableContent(
            isVisible = expandState == ExpandState.Expanded,
            content = {
                NegativePromptInputText(
                    text = negativePrompt,
                    onTextChange = onNegativePromptChange,
                    resetText = resetNegativePrompt,
                    charLimit = NewImageDescriptorViewModel.NEGATIVE_PROMPT_CHARACTER_LIMIT
                )
            }
        )
    }
}

@Composable
private fun NegativePromptInputText(
    text: String,
    onTextChange: (String) -> Unit,
    resetText: () -> Unit,
    modifier: Modifier = Modifier,
    charLimit: Int? = null
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color_Border,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        InputTextSection(
            text = text,
            placeHolder = stringResource(id = R.string.lbl_imazh_negative_prompt_placeholder),
            focusRequester = focusRequester,
            onTextChange = onTextChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClearTextIcon(
                enabled = text.isNotBlank(),
                clear = resetText
            )

            charLimit?.let {
                Text(
                    text = buildString {
                        append(text.length)
                        append("/")
                        append(it.toString())
                    },
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun InputTextSection(
    text: String,
    placeHolder: String,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        textStyle = MaterialTheme.typography.body1,
        placeholder = {
            Text(
                text = placeHolder,
                style = MaterialTheme.typography.body1,
                color = Color_Text_3,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = Color.Transparent
        ),
        maxLines = 5,
        minLines = 5,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )
}

@Composable
private fun Header(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    hasExpandButton: Boolean = false,
    expandState: ExpandState = ExpandState.Collapsed,
    onExpandClick: (currentExpandState: ExpandState) -> Unit = {},
    expandable: Boolean = false,
    hasInfo: Boolean = false,
    setupInfoTooltip: () -> Unit,
    onTooltipDismiss: () -> Unit,
    showInfoTooltip: Boolean,
    @StringRes infoTooltipMessage: Int? = null,
    sectionActionButton: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (hasExpandButton) {
            ExpandIcon(
                enabled = expandable,
                expandState = expandState,
                onClick = onExpandClick
            )
        }

        ViraBalloon(
            text = infoTooltipMessage?.let { stringResource(id = it) } ?: "",
            marginVertical = 0,
            marginHorizontal = 16,
            arrowPositionPercent = if (hasInfo) 0.1f else 0.5f,
            onDismiss = { onTooltipDismiss() }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.subtitle2,
                    color = LocalContentColor.current,
                    modifier = Modifier.then(
                        if (expandable) {
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                safeClick { onExpandClick(expandState) }
                            }
                        } else {
                            Modifier
                        }
                    )
                )

                infoTooltipMessage?.let {
                    if (hasInfo) {
                        IconButton(
                            onClick = {
                                safeClick(event = { setupInfoTooltip() })
                            }
                        ) {
                            ViraIcon(
                                drawable = R.drawable.ic_info,
                                contentDescription = stringResource(id = R.string.lbl_info),
                                tint = Color_Info_700
                            )
                        }
                    }
                    if (showInfoTooltip) {
                        showAlignBottom()
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )

        sectionActionButton?.invoke()
    }
}

@Composable
private fun ExpandIcon(
    enabled: Boolean,
    expandState: ExpandState,
    onClick: (currentExpandState: ExpandState) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(
        targetState = expandState,
        label = "transition"
    )

    val rotationDegree by transition.animateFloat(label = "expand degree") { state ->
        when (state) {
            ExpandState.Collapsed -> 0f
            ExpandState.Expanded -> 180f
        }
    }

    IconButton(
        onClick = {
            safeClick(event = { onClick(expandState) })
        },
        enabled = enabled,
        modifier = modifier
    ) {
        ViraIcon(
            drawable = R.drawable.ic_arrow_down,
            contentDescription = stringResource(id = R.string.lbl_details),
            modifier = Modifier.rotate(rotationDegree),
            tint = if (enabled) Cyan_200 else Color_Primary_Opacity_40
        )
    }
}

private enum class ExpandState {
    Expanded,
    Collapsed;

    fun toggleState(): ExpandState = when (this) {
        Expanded -> Collapsed
        Collapsed -> Expanded
    }
}

@Composable
private fun SectionActionButton(
    @StringRes stringRes: Int,
    @DrawableRes iconRes: Int,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .sizeIn(minWidth = 80.dp, minHeight = 32.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color_Primary_Opacity_15)
            .clickable {
                safeClick(event = action)
            }
            .padding(vertical = 8.dp)
            .padding(start = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ViraIcon(
            drawable = iconRes,
            contentDescription = stringResource(id = R.string.lbl_select),
            tint = Cyan_200
        )
        Text(
            text = stringResource(id = stringRes),
            style = MaterialTheme.typography.overline.copy(color = Cyan_200)
        )
    }
}

@Composable
private fun ClearTextIcon(
    enabled: Boolean,
    clear: () -> Unit
) {
    IconButton(
        onClick = {
            safeClick(event = clear)
        },
        enabled = enabled
    ) {
        ViraIcon(
            drawable = R.drawable.ic_clear_with_circle,
            contentDescription = stringResource(id = R.string.desc_clear),
            tint = Cyan_200.copy(alpha = LocalContentAlpha.current)
        )
    }
}

@ViraDarkPreview
@Composable
private fun ImazhNewImageDescriptorScreenPreview() {
    ViraPreview {
        ImazhNewImageDescriptorScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}