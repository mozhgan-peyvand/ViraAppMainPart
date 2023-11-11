package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.avasho.ui.archive.element.AudioImageStatus.Converting
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveProcessedFileElement
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveTrackingFileElement
import ai.ivira.app.features.avasho.ui.archive.element.AvashoArchiveUploadingFileElement
import ai.ivira.app.features.avasho.ui.archive.model.AvashoProcessedFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoTrackingFileView
import ai.ivira.app.features.avasho.ui.archive.model.AvashoUploadingFileView
import ai.ivira.app.features.avasho.ui.detail.AvashoDetailBottomSheet
import ai.ivira.app.features.avasho.ui.file_creation.SpeechResult
import ai.ivira.app.utils.data.NetworkStatus.Available
import ai.ivira.app.utils.data.NetworkStatus.Unavailable
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.AvaShoFileCreationScreen
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.BLue_a200_Opacity_40
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Card
import ai.ivira.app.utils.ui.theme.Color_Red
import ai.ivira.app.utils.ui.theme.Color_Red_800
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.Expanded
import androidx.compose.material.ModalBottomSheetValue.HalfExpanded
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.File

private const val TRACKING_FILE_ANIMATION_DURATION_COLUMN = 1300

@Composable
fun AvashoArchiveListScreen(
    navController: NavHostController,
    viewModel: AvashoArchiveListViewModel = hiltViewModel()
) {
    navController.currentBackStackEntry
        ?.savedStateHandle?.remove<SpeechResult>(SpeechResult.FILE_NAME)?.let {
            viewModel.addToQueue(
                fileName = it.fileName,
                speakerType = it.speakerType,
                text = it.text
            )
        }

    val archiveFiles by viewModel.allArchiveFiles.collectAsStateWithLifecycle(listOf())
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val uiViewState by viewModel.uiViewState.collectAsStateWithLifecycle(UiIdle)
    val isThereAnyTrackingOrUploading by viewModel.isThereAnyTrackingOrUploading.collectAsStateWithLifecycle()
    val brush = columnBrush()
    val coroutineScope = rememberCoroutineScope()

    var selectedAvashoItemBottomSheet by viewModel.selectedAvashoItemBottomSheet

    var bottomSheetInitialValue by viewModel.bottomSheetInitialValue

    val density = LocalDensity.current

    val bottomSheetState = rememberSaveable(
        inputs = arrayOf(density, bottomSheetInitialValue.name),
        key = bottomSheetInitialValue.name,
        saver = ModalBottomSheetState.Saver(
            density = density,
            animationSpec = SwipeableDefaults.AnimationSpec,
            skipHalfExpanded = false,
            confirmValueChange = { true }
        )
    ) {
        ModalBottomSheetState(
            density = density,
            initialValue = bottomSheetInitialValue,
            animationSpec = SwipeableDefaults.AnimationSpec,
            isSkipHalfExpanded = false,
            confirmValueChange = { true }
        )
    }

    BackHandler(bottomSheetState.isVisible) {
        bottomSheetInitialValue = when (bottomSheetState.currentValue) {
            Expanded -> {
                HalfExpanded
            }
            HalfExpanded -> {
                coroutineScope.launch {
                    if (bottomSheetState.isVisible) {
                        bottomSheetState.hide()
                    }
                }
                Hidden
            }
            else -> {
                return@BackHandler
            }
        }
    }

    val modalBottomSheetBorderShape =
        if (bottomSheetState.currentValue == HalfExpanded) {
            RoundedCornerShape(
                topEnd = 24.dp,
                topStart = 24.dp
            )
        } else {
            RoundedCornerShape(0.dp)
        }

    ModalBottomSheetLayout(
        sheetShape = modalBottomSheetBorderShape,
        sheetBackgroundColor = Color_BG_Bottom_Sheet,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        sheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            AvashoDetailBottomSheet(
                modifier = Modifier,
                collapseToolbarAction = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
                },
                avashoProcessedItem = selectedAvashoItemBottomSheet
            )
        }
    ) {
        Scaffold(
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                ArchiveAppBar(
                    onBackClick = {
                        safeClick {
                            navController.navigateUp()
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (archiveFiles.isEmpty()) {
                    ArchiveEmptyBody()
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val noNetworkAvailable = networkStatus is Unavailable
                        val hasVpnConnection = networkStatus.let { it is Available && it.hasVpn }
                        val isBannerError = uiViewState.let { it is UiError && !it.isSnack }

                        if (noNetworkAvailable || hasVpnConnection || isBannerError) {
                            if (isThereAnyTrackingOrUploading) {
                                ErrorBanner(
                                    errorMessage = if (uiViewState is UiError) {
                                        (uiViewState as UiError).message
                                    } else if (hasVpnConnection) {
                                        stringResource(id = string.msg_vpn_is_connected_error)
                                    } else {
                                        stringResource(id = string.msg_internet_disconnected)
                                    }
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(archiveFiles) {
                                when (it) {
                                    is AvashoProcessedFileView -> AvashoArchiveProcessedFileElement(
                                        archiveViewProcessed = it,
                                        isInDownloadQueue = viewModel.isInDownloadQueue(it.id),
                                        onItemClick = callback@{ item ->
                                            selectedAvashoItemBottomSheet = item
                                            if (File(item.filePath).exists()) {
                                                coroutineScope.launch {
                                                    if (!bottomSheetState.isVisible) {
                                                        bottomSheetState.show()
                                                    } else {
                                                        bottomSheetState.hide()
                                                    }
                                                }
                                                return@callback
                                            }

                                            if (viewModel.isInDownloadQueue(item.id)) {
                                                viewModel.cancelDownload(item.id)
                                            } else {
                                                viewModel.addFileToDownloadQueue(item)
                                            }
                                        },
                                        onIconClick = callback@{ processedItem ->
                                            if (File(processedItem.filePath).exists()) {
                                                coroutineScope.launch {
                                                    if (!bottomSheetState.isVisible) {
                                                        bottomSheetState.show()
                                                    } else {
                                                        bottomSheetState.hide()
                                                    }
                                                }
                                                return@callback
                                            }

                                            if (viewModel.isInDownloadQueue(processedItem.id)) {
                                                viewModel.cancelDownload(processedItem.id)
                                            } else {
                                                viewModel.addFileToDownloadQueue(processedItem)
                                            }
                                        }
                                    )

                                    is AvashoTrackingFileView -> {
                                        AvashoArchiveTrackingFileElement(
                                            archiveTrackingView = it,
                                            brush = brush,
                                            estimateTime = { it.computeFileEstimateProcess() },
                                            audioImageStatus = Converting
                                        )
                                    }

                                    is AvashoUploadingFileView -> {
                                        AvashoArchiveUploadingFileElement(
                                            avashoUploadingFileView = it,
                                            isNetworkAvailable = !noNetworkAvailable && !hasVpnConnection,
                                            isErrorState = uiViewState.let { uiStatus ->
                                                (uiStatus is UiError) && !uiStatus.isSnack
                                            },
                                            onTryAgainClick = { uploadingItem ->
                                                viewModel.startUploading(uploadingItem)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Fab(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onMainFabClick = {
                        navController.navigate(route = AvaShoFileCreationScreen.route)
                    }
                )
            }
        }
    }
}

// fixme it's duplicate, in [AvaNegarArchiveListScreen]
@Composable
private fun ErrorBanner(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color_Red_800)
            .padding(8.dp)
    ) {
        ViraIcon(
            drawable = drawable.ic_failure_network,
            contentDescription = null,
            tint = Color_Red
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.body2,
            color = Color_Red
        )
    }
}

@Composable
private fun ArchiveEmptyBody(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(0.7f),
            verticalArrangement = Arrangement.Bottom
        ) {
            ViraImage(
                drawable = drawable.img_main_page,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = string.lbl_dose_not_exist_any_file),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = string.lbl_make_your_first_file),
                style = MaterialTheme.typography.caption,
                color = Color_Text_3,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(53.dp))

        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(80.dp))

            ViraImage(
                drawable = drawable.ic_arrow,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )
        }

        Spacer(modifier = Modifier.size(60.dp))
    }
}

@Composable
private fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
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
                safeClick {
                    onBackClick()
                }
            }
        ) {
            ViraIcon(
                drawable = drawable.ic_arrow_forward,
                modifier = Modifier.padding(12.dp),
                contentDescription = stringResource(id = string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = string.lbl_ava_sho),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun Fab(
    modifier: Modifier = Modifier,
    onMainFabClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp
        )
    ) {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.clip(CircleShape),
            onClick = {
                safeClick {
                    onMainFabClick()
                }
            }
        ) {
            ViraIcon(
                drawable = drawable.ic_add,
                contentDescription = null,
                tint = Color_White
            )
        }
    }
}

@Composable
private fun columnBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0.01f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = TRACKING_FILE_ANIMATION_DURATION_COLUMN,
                easing = EaseInOut
            ),
            repeatMode = Reverse
        ),
        label = ""
    )

    return remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height
                return LinearGradientShader(
                    colors = listOf(BLue_a200_Opacity_40, Color_Card),
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, size.height),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun AvashoArchiveListScreenPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvashoArchiveListScreen(navController = rememberNavController())
        }
    }
}