package ai.ivira.app.features.imazh.ui.details

import ai.ivira.app.R
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheet
import ai.ivira.app.designsystem.bottomsheet.ViraBottomSheetContent
import ai.ivira.app.designsystem.bottomsheet.rememberViraBottomSheetState
import ai.ivira.app.features.ava_negar.ui.archive.sheets.AccessDeniedToOpenFileBottomSheet
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.ui.ImazhAnalytics
import ai.ivira.app.features.imazh.ui.details.ImazhDetailBottomSheetType.DeleteConfirmation
import ai.ivira.app.features.imazh.ui.details.ImazhDetailBottomSheetType.FileAccessPermissionDenied
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhStyleItem
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.convertByteToMB
import ai.ivira.app.utils.ui.hasPermission
import ai.ivira.app.utils.ui.isPermissionDeniedPermanently
import ai.ivira.app.utils.ui.isSdkVersionBetween23And29
import ai.ivira.app.utils.ui.navigateToAppSettings
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.showMessage
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_On_Surface
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImazhDetailsScreenRoute(navController: NavHostController) {
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(ImazhAnalytics.screenViewDetails)
    }

    ImazhDetailsScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun ImazhDetailsScreen(
    navController: NavHostController,
    viewModel: ImazhDetailsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackBarState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarState)
    val sheetState = rememberViraBottomSheetState()

    var selectedSheet by rememberSaveable { mutableStateOf(DeleteConfirmation) }

    val scrollState = rememberScrollState()
    val photoInfo by viewModel.archiveFile.collectAsStateWithLifecycle()

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

    LaunchedEffect(Unit) {
        viewModel.uiViewState.collectLatest {
            if (it is UiError && it.isSnack) {
                sheetState.hide()
                delay(100)
                showMessage(
                    snackBarState,
                    coroutineScope,
                    it.message
                )
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color_BG,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopBar(
                onBackClick = navController::navigateUp,
                onDeleteClick = {
                    selectedSheet = DeleteConfirmation
                    sheetState.show()
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
                            selectedSheet = FileAccessPermissionDenied
                            sheetState.show()
                        }
                    } else {
                        // Asking for permission
                        writeStoragePermissionLauncher.launch(writeStoragePermission)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) bodyColumn@{
                val info = photoInfo ?: return@bodyColumn
                val file = File(info.filePath)

                AsyncImage(
                    model = info.filePath,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                Column(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                ) {
                    ImageDescription(
                        description = info.prompt,
                        createdAt = info.createdAt,
                        imageSize = file.length().toDouble()
                    )

                    if (info.keywords.isNotEmpty()) {
                        Keyword(list = info.keywords)
                    }

                    if (info.negativePrompt.isNotEmpty()) {
                        NegativePrompt(value = info.negativePrompt)
                    }

                    if (info.style != ImazhImageStyle.None) {
                        Style(style = info.style)
                    }
                }
            }
        }
    }

    if (sheetState.showBottomSheet) {
        ViraBottomSheet(sheetState = sheetState) {
            ViraBottomSheetContent(selectedSheet) sheetContent@{ selected ->
                when (selected) {
                    DeleteConfirmation -> {
                        val info = photoInfo ?: return@sheetContent

                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                viewModel.removeImage(info.id, info.filePath)
                                sheetState.hide()
                                navController.navigateUp()
                            },
                            cancelAction = {
                                sheetState.hide()
                            },
                            fileName = ""
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
                }
            }
        }
    }
}

@Composable
private fun ImageDescription(
    description: String,
    createdAt: String,
    imageSize: Double
) {
    Section(
        title = R.string.lbl_image_prompt,
        modifier = Modifier.padding(top = 30.dp),
        content = {
            Spacer(modifier = Modifier.size(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ViraIcon(
                    drawable = R.drawable.ic_calendar,
                    contentDescription = null,
                    tint = Color_Primary_200
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    color = Color_Text_3,
                    style = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
                    text = createdAt
                )

                Spacer(modifier = Modifier.size(32.dp))

                if (imageSize != -1.0) {
                    Text(
                        text = buildString {
                            append(stringResource(id = R.string.lbl_size))
                            append(
                                convertByteToMB(
                                    imageSize.orZero()
                                )
                            )
                            append(stringResource(id = R.string.lbl_mb))
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color_Text_1
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            SelectionContainer {
                Text(
                    text = description,
                    color = Color_Text_3,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    )
}

@Composable
private fun Keyword(list: List<String>) {
    Section(
        title = R.string.lbl_image_attributes,
        modifier = Modifier.padding(top = 24.dp),
        content = {
            Spacer(modifier = Modifier.size(20.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                list.forEach { imageStyle ->
                    Text(
                        text = imageStyle,
                        color = Color_On_Surface,
                        style = MaterialTheme.typography.button,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color_Primary_Opacity_15, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun NegativePrompt(value: String) {
    Section(
        title = R.string.lbl_negative_prompt,
        modifier = Modifier.padding(top = 20.dp),
        content = {
            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = value,
                color = Color_Text_3,
                style = MaterialTheme.typography.body1
            )
        }
    )
}

@Composable
private fun Style(style: ImazhImageStyle) {
    Section(
        title = R.string.lbl_image_style,
        modifier = Modifier.padding(top = 12.dp),
        content = {
            Spacer(modifier = Modifier.size(20.dp))

            ImazhStyleItem(
                style = style,
                isSelected = false,
                showBorderOnSelection = false,
                onItemClick = null
            )
        }
    )
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
            text = stringResource(id = R.string.lbl_image_explanations),
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
private fun Section(
    @StringRes title: Int,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.subtitle1,
            color = Color_Text_1
        )

        content()
    }
}

@ViraDarkPreview
@Composable
private fun ImazhDetailsScreenPreview() {
    ViraPreview {
        ImazhDetailsScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}