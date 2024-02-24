package ai.ivira.app.features.imazh.ui.details

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.archive.sheets.FileItemConfirmationDeleteBottomSheet
import ai.ivira.app.features.imazh.data.ImazhImageStyle
import ai.ivira.app.features.imazh.ui.details.ImazhDetailBottomSheetType.DeleteConfirmation
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhStyleItem
import ai.ivira.app.utils.common.orZero
import ai.ivira.app.utils.ui.convertByteToMB
import ai.ivira.app.utils.ui.hide
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_On_Surface
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Primary_Opacity_15
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.toBitmap
import ai.ivira.app.utils.ui.widgets.ViraIcon
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImazhDetailsScreenRoute(navController: NavHostController) {
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
    val scaffoldState = rememberScaffoldState()
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { true }
    )

    var selectedSheet by rememberSaveable { mutableStateOf(DeleteConfirmation) }

    val scrollState = rememberScrollState()
    val photoInfo by viewModel.archiveFile.collectAsStateWithLifecycle()

    BackHandler(modalBottomSheetState.isVisible) {
        modalBottomSheetState.hide(coroutineScope)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color_BG,
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) { paddingValues ->
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetState = modalBottomSheetState,
            sheetContent = sheetContent@{
                when (selectedSheet) {
                    DeleteConfirmation -> {
                        val info = photoInfo ?: return@sheetContent

                        FileItemConfirmationDeleteBottomSheet(
                            deleteAction = {
                                viewModel.removeImage(info.id, info.filePath)
                                modalBottomSheetState.hide(coroutineScope)
                                navController.navigateUp()
                            },
                            cancelAction = {
                                modalBottomSheetState.hide(coroutineScope)
                            },
                            fileName = ""
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TopBar(
                    onBackClick = navController::navigateUp,
                    onDeleteClick = {
                        selectedSheet = DeleteConfirmation
                        coroutineScope.launch {
                            modalBottomSheetState.show()
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
                    val imageBitmap: Bitmap? by remember(file) { mutableStateOf(file.toBitmap()) }

                    imageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(top = 8.dp)
                        )
                    }

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
        content = {
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
                    style = MaterialTheme.typography.body2,
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
        content = {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                list.forEach { imageStyle ->
                    Text(
                        text = imageStyle,
                        color = Color_On_Surface,
                        style = MaterialTheme.typography.button,
                        modifier = Modifier
                            .padding(top = 8.dp, end = 8.dp)
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
        content = {
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
        content = {
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
    }
}

@Composable
private fun Section(
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.subtitle1,
            color = Color_Text_1
        )

        Spacer(modifier = Modifier.size(20.dp))

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