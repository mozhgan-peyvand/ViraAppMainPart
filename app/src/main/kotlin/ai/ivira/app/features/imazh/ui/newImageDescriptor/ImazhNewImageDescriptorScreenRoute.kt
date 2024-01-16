package ai.ivira.app.features.imazh.ui.newImageDescriptor

import ai.ivira.app.R
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.ui.newImageDescriptor.NewImageDescriptorViewModel.Companion.NEGATIVE_PROMPT_CHARACTER_LIMIT
import ai.ivira.app.features.imazh.ui.newImageDescriptor.NewImageDescriptorViewModel.Companion.PROMPT_CHARACTER_LIMIT
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhKeywordItem
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhStyleItem
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.HistoryBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhKeywordBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.BackWhileEditing
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.BackWhileGenerate
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.History
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.Keywords
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.RandomPrompt
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.ImazhNewImageDescriptionBottomSheetType.Style
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.RandomConfirmationBottomSheet
import ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets.SelectStyleBottomSheet
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiLoading
import ai.ivira.app.utils.ui.UiSuccess
import ai.ivira.app.utils.ui.hide
import ai.ivira.app.utils.ui.hideAndShow
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.show
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Border
import ai.ivira.app.utils.ui.theme.Color_Info_700
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_40
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Cyan_200
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun ImazhNewImageDescriptorScreenRoute(navController: NavHostController) {
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
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState: ScrollState = rememberScrollState()
    val imazhKeywords by viewModel.imazhKeywords.collectAsStateWithLifecycle()
    val isOkToGenerate by remember {
        derivedStateOf { viewModel.prompt.value.isNotBlank() && !isLoading }
    }
    val view = LocalView.current
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val isHistoryButtonVisible by remember(viewModel.historyList.value) {
        mutableStateOf(viewModel.historyList.value.isNotEmpty())
    }
    val availableStyles by viewModel.availableStyles.collectAsState(initial = emptyList())

    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(History)
    }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { true }
    )

    LaunchedEffect(modalBottomSheetState.isVisible) {
        if (modalBottomSheetState.isVisible) {
            focusManager.clearFocus()
        }
    }

    BackHandler {
        if (modalBottomSheetState.isVisible) {
            modalBottomSheetState.hide(coroutineScope)
        } else {
            viewModel.handelBackButton(
                navigateUp = {
                    navController.navigateUp()
                },
                backWhileEditing = {
                    setSelectedSheet(BackWhileEditing)
                    modalBottomSheetState.show(coroutineScope)
                },
                backWhileGenerating = {
                    setSelectedSheet(BackWhileGenerate)
                    modalBottomSheetState.show(coroutineScope)
                }
            )
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
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiError -> {
                showMessage(
                    snackbarHostState,
                    coroutineScope,
                    context.getString(R.string.msg_updating_failed_please_try_again_later)
                )

                // fixme should remove it, replace stateFlow with sharedFlow in viewModel
                viewModel.clearUiState()
            }

            is UiLoading -> {
                isLoading = true
            }
            is UiSuccess -> {
                navController.navigateUp()
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            NewImageDescriptorTopBar(
                onBackClick = navController::navigateUp
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) { paddingValues ->
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = modalBottomSheetState,
            sheetContent = {
                when (selectedSheet) {
                    History -> {
                        HistoryBottomSheet(
                            onAcceptClick = { prompt ->
                                viewModel.changePrompt(prompt)
                                coroutineScope.launch {
                                    if (modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            }
                        )
                    }
                    RandomPrompt -> {
                        RandomConfirmationBottomSheet(
                            cancelAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            deleteAction = {
                                viewModel.resetPrompt()
                                viewModel.generateRandomPrompt(
                                    confirmationCallback = {
                                        modalBottomSheetState.hideAndShow(coroutineScope)
                                    }
                                )
                                modalBottomSheetState.hide(coroutineScope)
                            }
                        )
                    }
                    Style -> {
                        SelectStyleBottomSheet(
                            styles = availableStyles,
                            selectedStyle = viewModel.selectedStyle.value,
                            confirmSelectionCallBack = {
                                viewModel.selectStyle(it)
                                modalBottomSheetState.hide(coroutineScope)
                            }
                        )
                    }
                    Keywords -> {
                        ImazhKeywordBottomSheet(
                            map = imazhKeywords,
                            selectedChips = viewModel.selectedKeywords.value,
                            onAcceptClick = { list ->
                                viewModel.updateKeywordList(list)
                                coroutineScope.launch {
                                    if (modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            }
                        )
                    }
                    BackWhileGenerate -> {
                        BackConfirmationWhileGeneratingBottomSheet(
                            continueAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            deleteAction = {
                                viewModel.cancelConvertTextToImageRequest()
                                modalBottomSheetState.hide(coroutineScope)
                                navController.navigateUp()
                            })
                    }
                    BackWhileEditing -> {
                        BackConfirmationWhileEditingBottomSheet(
                            generateAction = {
                                viewModel.sendRequest(
                                    prompt = "",
                                    negativePrompt = "",
                                    keywords = emptyList(),
                                    style = viewModel.selectedStyle.value
                                )
                            },
                            deleteAction = {
                                modalBottomSheetState.hide(coroutineScope)
                                navController.navigateUp()
                            })
                    }
                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(state = scrollState)
                        .padding(paddingValues = paddingValues)
                        .then(
                            if (isKeyboardVisible) {
                                Modifier.padding(bottom = 16.dp)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Prompt(
                        prompt = viewModel.prompt.value,
                        isHistoryButtonVisible = isHistoryButtonVisible,
                        onPromptChange = viewModel::changePrompt,
                        resetPrompt = viewModel::resetPrompt,
                        onHistoryClick = {
                            coroutineScope.launch {
                                setSelectedSheet(History)
                                modalBottomSheetState.show()
                            }
                        },
                        onRandomClick = {
                            viewModel.generateRandomPrompt(
                                confirmationCallback = {
                                    setSelectedSheet(RandomPrompt)
                                    modalBottomSheetState.show(coroutineScope)
                                }
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Keywords(
                        keywords = viewModel.selectedKeywords.value.toList(),
                        onAddKeywordClick = {
                            coroutineScope.launch {
                                setSelectedSheet(Keywords)
                                modalBottomSheetState.show()
                            }
                        },
                        onChipClick = { item ->
                            viewModel.removeKeyword(item)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Style(
                        selectedStyle = viewModel.selectedStyle.value,
                        isExpandable = viewModel.selectedStyle.value != ImazhImageStyle.None,
                        openStyleBottomSheet = {
                            setSelectedSheet(Style)
                            modalBottomSheetState.show(coroutineScope)
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
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (!isKeyboardVisible) {
                    ConfirmButton(
                        onClick = {
                            viewModel.sendRequest(
                                prompt = "",
                                negativePrompt = "",
                                keywords = emptyList(),
                                style = viewModel.selectedStyle.value
                            )
                        },
                        enabled = isOkToGenerate,
                        isLoading = isLoading,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
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

@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 20.dp)
    ) {
        Button(
            contentPadding = PaddingValues(vertical = 14.dp),
            onClick = {
                safeClick(event = onClick)
            },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(id = R.string.lbl_generate_image),
                    style = MaterialTheme.typography.button,
                    color = Color_Text_1
                )
            }
        }
    }
}

@Composable
private fun Prompt(
    prompt: String,
    isHistoryButtonVisible: Boolean,
    onPromptChange: (String) -> Unit,
    resetPrompt: () -> Unit,
    onHistoryClick: () -> Unit,
    onRandomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Header(
            title = R.string.lbl_image_prompt,
            hasInfo = false
        )
        PromptInputText(
            prompt = prompt,
            isHistoryButtonVisible = isHistoryButtonVisible,
            onPromptChange = onPromptChange,
            resetPrompt = resetPrompt,
            charLimit = PROMPT_CHARACTER_LIMIT,
            onHistoryClick = onHistoryClick,
            onRandomClick = onRandomClick
        )
    }
}

@Composable
private fun PromptInputText(
    prompt: String,
    isHistoryButtonVisible: Boolean,
    onPromptChange: (String) -> Unit,
    resetPrompt: () -> Unit,
    onRandomClick: () -> Unit,
    onHistoryClick: () -> Unit,
    charLimit: Int? = null
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color_Border,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        InputTextSection(
            text = prompt,
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
        }
    }
}

@Composable
private fun Keywords(
    keywords: List<ImazhKeywordView>,
    onAddKeywordClick: () -> Unit,
    onChipClick: (String) -> Unit,
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

    Header(
        title = R.string.lbl_keyword,
        hasExpandButton = true,
        expandState = expandState,
        expandable = keywords.isNotEmpty(),
        onExpandClick = { expandState = expandState.toggleState() },
        hasInfo = false,
        onInfoClick = {
            // TODO: Should open keywords info here
        },
        sectionActionButton = {
            SectionActionButton(
                stringRes = R.string.lbl_add,
                iconRes = R.drawable.ic_add_small,
                action = {
                    onAddKeywordClick()
                }
            )
        },
        modifier = modifier
    )

    if (expandState == ExpandState.Expanded) {
        Row {
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    key = { keywordView -> keywordView.farsi },
                    items = keywords
                ) { list ->
                    ImazhKeywordItem(
                        value = list,
                        isSelected = true,
                        onClick = { onChipClick(list.farsi) }
                    )
                }
            }
        }
    }
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
            hasInfo = false,
            onInfoClick = {},
            onExpandClick = { expandState = expandState.toggleState() },
            sectionActionButton = {
                SectionActionButton(
                    stringRes = if (selectedStyle != ImazhImageStyle.None) R.string.lbl_change else R.string.lbl_select,
                    iconRes = if (selectedStyle != ImazhImageStyle.None) R.drawable.ic_change else R.drawable.ic_add_small,
                    action = openStyleBottomSheet
                )
            }
        )
        if (expandState == ExpandState.Expanded && selectedStyle != ImazhImageStyle.None) {
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
    }
}

@Composable
private fun NegativePrompt(
    negativePrompt: String,
    onNegativePromptChange: (String) -> Unit,
    resetNegativePrompt: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandState by rememberSaveable { mutableStateOf(ExpandState.Collapsed) }

    Column(modifier = modifier) {
        Header(
            title = R.string.lbl_negative_prompt,
            hasExpandButton = true,
            expandState = expandState,
            expandable = true,
            onExpandClick = { expandState = it.toggleState() },
            hasInfo = false
        )

        if (expandState == ExpandState.Expanded) {
            NegativePromptInputText(
                text = negativePrompt,
                onTextChange = onNegativePromptChange,
                resetText = resetNegativePrompt,
                charLimit = NEGATIVE_PROMPT_CHARACTER_LIMIT
            )
        }
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
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        textStyle = MaterialTheme.typography.body1,
        placeholder = {
            Text(
                text = stringResource(id = R.string.msg_describe_your_image),
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
    onInfoClick: () -> Unit = {},
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

        if (hasInfo) {
            IconButton(
                onClick = {
                    safeClick(event = onInfoClick)
                }
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_info,
                    contentDescription = stringResource(id = R.string.lbl_info),
                    tint = Color_Info_700
                )
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
    val rotationDegree = when (expandState) {
        ExpandState.Collapsed -> {
            0f
        }
        ExpandState.Expanded -> {
            180f
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
            style = MaterialTheme.typography.labelMedium.copy(
                color = Cyan_200
            )
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