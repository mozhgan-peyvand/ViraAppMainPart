package ir.part.app.intelligentassistant.features.ava_negar.ui.search

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.ava_negar.ui.SnackBar
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.ArchiveBottomSheetType
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.DeleteBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.element.ArchiveProcessedFileElementGrid
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.DetailItemBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.RenameFileBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.archive.sheets.ShareDetailItemBottomSheet
import ir.part.app.intelligentassistant.features.ava_negar.ui.details.TIME_INTERVAL
import ir.part.app.intelligentassistant.utils.common.file.convertTextToPdf
import ir.part.app.intelligentassistant.utils.common.file.convertTextToTXTFile
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.safeClick
import ir.part.app.intelligentassistant.utils.ui.sharePdf
import ir.part.app.intelligentassistant.utils.ui.shareTXT
import ir.part.app.intelligentassistant.utils.ui.shareText
import ir.part.app.intelligentassistant.utils.ui.showMessage
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG
import ir.part.app.intelligentassistant.utils.ui.theme.Color_BG_Bottom_Sheet
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Color_White
import ir.part.app.intelligentassistant.utils.ui.theme.labelMedium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun AvaNegarSearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val searchResult by viewModel.getSearchResult.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val localClipBoardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    var isConvertingPdf by rememberSaveable { mutableStateOf(false) }
    var isConvertingTxt by rememberSaveable { mutableStateOf(false) }
    var shouldSharePdf by rememberSaveable { mutableStateOf(false) }
    var shouldShareTxt by rememberSaveable { mutableStateOf(false) }

    //default value is true because we want to open keyboard when the screen created
    val shouldShowKeyBoard = rememberSaveable { mutableStateOf(true) }
    val fileName = rememberSaveable { mutableStateOf("") }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { !isConvertingPdf && !isConvertingTxt }
    )

    val (selectedSheet, setSelectedSheet) = rememberSaveable {
        mutableStateOf(
            SearchBottomSheetType.Delete
        )
    }

    var backPressedInterval: Long = 0

    BackHandler(modalBottomSheetState.isVisible) {

        if (modalBottomSheetState.isVisible) {
            coroutineScope.launch(IO) {
                if (!isConvertingPdf && !isConvertingTxt)
                    modalBottomSheetState.hide()
                else {
                    if (backPressedInterval + TIME_INTERVAL < System.currentTimeMillis()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.msg_back_again_to_cancel_converting),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        backPressedInterval = System.currentTimeMillis()
                    } else {
                        withContext(Dispatchers.Main) {
                            isConvertingPdf = false
                            isConvertingTxt = false
                            modalBottomSheetState.hide()
                        }
                    }
                }
            }
        } else {
            navHostController.navigateUp()
        }
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(modalBottomSheetState.currentValue) {

        if (modalBottomSheetState.isVisible) {
            if (selectedSheet.name == ArchiveBottomSheetType.Rename.name)
                shouldShowKeyBoard.value = true
        } else {
            shouldShowKeyBoard.value = false
        }
    }

    LaunchedEffect(isConvertingPdf) {
        if (isConvertingPdf) {

            viewModel.jobConverting?.cancel()
            viewModel.jobConverting = coroutineScope.launch(IO) {
                viewModel.fileToShare = convertTextToPdf(
                    context = context,
                    text = viewModel.processItem?.text.orEmpty(),
                    fileName = fileName.value
                )

                shouldSharePdf = true
                isConvertingPdf = false
            }

        } else viewModel.jobConverting?.cancel()
    }

    LaunchedEffect(isConvertingTxt) {
        if (isConvertingTxt) {

            viewModel.jobConverting?.cancel()
            viewModel.jobConverting = coroutineScope.launch(IO) {
                viewModel.fileToShare = convertTextToTXTFile(
                    context = context,
                    text = viewModel.processItem?.text.orEmpty(),
                    fileName = fileName.value
                )

                shouldShareTxt = true
                isConvertingTxt = false

            }

        } else viewModel.jobConverting?.cancel()
    }

    LaunchedEffect(shouldSharePdf) {
        if (shouldSharePdf) {
            modalBottomSheetState.hide()
            viewModel.fileToShare?.let {
                sharePdf(context = context, file = it)
                shouldSharePdf = false
            }
        }
    }

    LaunchedEffect(shouldShareTxt) {
        if (shouldShareTxt) {
            modalBottomSheetState.hide()
            viewModel.fileToShare?.let {
                shareTXT(context = context, file = it)
                shouldShareTxt = false
            }
        }
    }


    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackBar(it)
        },
    ) { innerPadding ->

        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = modalBottomSheetState,
            sheetContent = {
                when (selectedSheet) {

                    SearchBottomSheetType.Rename -> {
                        RenameFileBottomSheet(
                            fileName = fileName.value,
                            shouldShowKeyBoard = shouldShowKeyBoard.value,
                            onValueChange = { fileName.value = it },
                            reNameAction = {
                                viewModel.updateTitle(
                                    title = fileName.value,
                                    id = viewModel.processItem?.id
                                )

                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    SearchBottomSheetType.Detail -> {
                        DetailItemBottomSheet(
                            text = viewModel.processItem?.title.orEmpty(),
                            copyItemAction = {
                                localClipBoardManager.setText(
                                    AnnotatedString(
                                        viewModel.processItem?.text.orEmpty()
                                    )
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }

                                showMessage(
                                    snackbarHostState,
                                    coroutineScope,
                                    context.getString(R.string.lbl_text_save_in_clipboard)
                                )
                            },
                            shareItemAction = {
                                setSelectedSheet(SearchBottomSheetType.Share)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            },
                            renameItemAction = {
                                setSelectedSheet(SearchBottomSheetType.Rename)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }

                                }
                            },
                            deleteItemAction = {
                                setSelectedSheet(SearchBottomSheetType.DeleteConfirmation)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            },
                        )
                    }

                    SearchBottomSheetType.Share -> {
                        ShareDetailItemBottomSheet(
                            isConverting = isConvertingPdf || isConvertingTxt,
                            onPdfClick = { isConvertingPdf = true },
                            onTextClick = { isConvertingTxt = true },
                            onOnlyTextClick = {
                                shareText(
                                    context = context,
                                    text = viewModel.processItem?.text.orEmpty()
                                )
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                        )
                    }

                    SearchBottomSheetType.DeleteConfirmation -> {
                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                viewModel.removeProcessedFile(viewModel.processItem?.id)

                                File(
                                    viewModel.processItem?.filePath.orEmpty()
                                ).delete()
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            cancelAction = {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            },
                            fileName = viewModel.processItem?.title.orEmpty()
                        )
                    }

                    SearchBottomSheetType.Delete -> {
                        DeleteBottomSheet(
                            fileName = viewModel.archiveViewItem?.title.orEmpty(),
                            onDelete = {
                                setSelectedSheet(SearchBottomSheetType.DeleteConfirmation)
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                    if (!modalBottomSheetState.isVisible) {
                                        modalBottomSheetState.show()
                                    } else {
                                        modalBottomSheetState.hide()
                                    }
                                }
                            })
                    }
                }
            }
        ) {
            AvaNegarSearchBody(
                searchText = searchText,
                searchResult = searchResult,
                focusRequester = focusRequester,
                arrowForwardAction = {
                    navHostController.popBackStack()
                },
                onValueChangeAction = {
                    viewModel.onSearchTextChange(it)
                },
                clearState = {
                    viewModel.onSearchTextChange("")
                },
                isSearch = isSearching,
                modifier = Modifier.padding(innerPadding),
                onMenuClick = {
                    setSelectedSheet(SearchBottomSheetType.Detail)
                    coroutineScope.launch {
                        if (!modalBottomSheetState.isVisible) {
                            modalBottomSheetState.show()
                        } else {
                            modalBottomSheetState.hide()
                        }
                    }
                    viewModel.archiveViewItem = it
                    viewModel.processItem = it
                    fileName.value = it.title
                },
                onItemClick = {
                    navHostController.navigate(
                        ScreenRoutes.AvaNegarArchiveDetail.route.plus(
                            "/$it"
                        )
                    )
                }
            )
        }
    }


}

@Composable
private fun AvaNegarSearchBody(
    modifier: Modifier = Modifier,
    searchText: String,
    searchResult: List<AvanegarProcessedFileView>,
    focusRequester: FocusRequester,
    arrowForwardAction: () -> Unit,
    onValueChangeAction: (String) -> Unit,
    clearState: () -> Unit, isSearch: Boolean,
    onMenuClick: (AvanegarProcessedFileView) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_loading)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = true
    )
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchToolbar(
            searchText = searchText,
            focusRequester = focusRequester,
            arrowForwardAction = arrowForwardAction,
            onValueChangeAction = onValueChangeAction,
            clearState = clearState
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (searchResult.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = modifier
                        .padding(vertical = 20.dp)
                        .fillMaxSize(),
                    columns = GridCells.Adaptive(128.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = searchResult,
                    ) { item ->
                        ArchiveProcessedFileElementGrid(
                            archiveViewProcessed = item,
                            onItemClick = { onItemClick(it) },
                            onMenuClick = { onMenuClick(it) }
                        )
                    }
                }
            }

            if (isSearch && searchText.isNotBlank()) {
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchToolbar(
    searchText: String,
    focusRequester: FocusRequester,
    arrowForwardAction: () -> Unit,
    onValueChangeAction: (String) -> Unit,
    clearState: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp, bottom = 8.dp, end = 16.dp, start = 8.dp
            ), verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            safeClick {
                arrowForwardAction()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = stringResource(id = R.string.desc_forward),
                modifier = Modifier.padding(12.dp)
            )
        }
        TextField(
            value = searchText,
            textStyle = MaterialTheme.typography.body2,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .border(
                    1.dp,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colors.primary
                ),
            onValueChange = { onValueChangeAction(it) },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.lbl_search_in_archive),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Image(
                    painterResource(id = R.drawable.ic_search_n),
                    contentDescription = stringResource(id = R.string.desc_share),
                    modifier = Modifier.padding(10.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    safeClick {
                        clearState()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = stringResource(id = R.string.desc_clear),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                trailingIconColor = Color_White,
                leadingIconColor = Color_White,
                textColor = Color_Text_1,
                placeholderColor = Color_Text_3
            ),
        )
    }
}