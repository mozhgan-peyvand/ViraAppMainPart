package ai.ivira.app.features.imazh.ui.archive

import ai.ivira.app.R
import ai.ivira.app.features.imazh.ui.archive.model.ImazhArchiveView
import ai.ivira.app.features.imazh.ui.archive.model.ImazhProcessedFileView
import ai.ivira.app.features.imazh.ui.newImageDescriptor.KEY_NEW_IMAGE_RESULT
import ai.ivira.app.utils.data.NetworkStatus
import ai.ivira.app.utils.ui.UiError
import ai.ivira.app.utils.ui.UiIdle
import ai.ivira.app.utils.ui.ViraAsyncImageUsingCoil
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.navigation.ScreenRoutes.ImazhNewImageDescriptorScreen
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Blue_Grey_800_945
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraBannerInfo
import ai.ivira.app.utils.ui.widgets.ViraBannerWithAnimation
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun ImazhArchiveListScreenRoute(navController: NavHostController) {
    ImazhArchiveListScreen(
        navController = navController,
        viewModel = hiltViewModel()
    )
}

@Composable
private fun ImazhArchiveListScreen(
    navController: NavHostController,
    viewModel: ImazhArchiveListViewModel
) {
    val isGrid by viewModel.isGrid.collectAsState()
    val archiveFiles by viewModel.allArchiveFiles.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val uiViewState by viewModel.uiViewState.collectAsState(UiIdle)
    var isVisible by rememberSaveable { mutableStateOf(true) }
    val isScrollingUp by listState.isScrollingUp()
    val isScrolledDown by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                listState.firstVisibleItemScrollOffset > 0
        }
    }

    val newImageResult = getNewImageResult(navController)?.collectAsState(initial = false)
    LaunchedEffect(newImageResult, isScrolledDown) {
        if (newImageResult?.value == true && isScrolledDown) {
            listState.scrollToItem(0)
            resetNewImageResult(navController)
        }
    }

    LaunchedEffect(isGrid) { isVisible = true }
    LaunchedEffect(isScrollingUp) { isVisible = isScrollingUp }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            ImazhArchiveAppBar(
                onBackClick = navController::navigateUp,
                isGrid = isGrid,
                showListTypeIcon = archiveFiles.isNotEmpty(),
                onChangeListTypeClick = {
                    viewModel.saveListType(!isGrid)
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
                Column {
                    // TODO: Don't show it when download state is added
                    val isNetworkAvailable by remember(networkStatus) {
                        mutableStateOf(networkStatus is NetworkStatus.Available)
                    }
                    val hasVpnConnection by remember(networkStatus) {
                        mutableStateOf(networkStatus.let { it is NetworkStatus.Available && it.hasVpn })
                    }
                    val isBannerError by remember(uiViewState) {
                        mutableStateOf(uiViewState.let { it is UiError && !it.isSnack })
                    }
                    ViraBannerWithAnimation(
                        isVisible = !isNetworkAvailable || hasVpnConnection || isBannerError, // FIXME: Should this be displaying only if upload is in progress?
                        bannerInfo = if (uiViewState is UiError) {
                            ViraBannerInfo.Error(
                                message = (uiViewState as UiError).message,
                                iconRes = R.drawable.ic_failure_network
                            )
                        } else if (hasVpnConnection) {
                            ViraBannerInfo.Warning(
                                message = stringResource(id = R.string.msg_vpn_is_connected_error),
                                iconRes = R.drawable.ic_warning_vpn
                            )
                        } else {
                            ViraBannerInfo.Error(
                                message = stringResource(id = R.string.msg_internet_disconnected),
                                iconRes = R.drawable.ic_failure_network
                            )
                        }
                    )

                    ArchiveListContent(
                        archiveFiles = archiveFiles,
                        listState = listState,
                        isGrid = isGrid,
                        imageBuilder = { urlPath ->
                            viewModel.getImageBuilder(imageBuilder = this, urlPath = urlPath)
                        },
                        onProcessedItemClick = { id ->
                            navController.navigate(ScreenRoutes.ImazhDetailsScreen.createRoute(id))
                        }
                    )
                }
            }

            ImazhArchiveFab(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = onClick@{
                    navController.navigate(route = ImazhNewImageDescriptorScreen.route)
                },
                isVisible = isVisible
            )
        }
    }
}

@Composable
private fun ArchiveListContent(
    archiveFiles: List<ImazhArchiveView>,
    listState: LazyGridState,
    isGrid: Boolean,
    imageBuilder: ImageRequest.Builder.(urlPath: String) -> ImageRequest.Builder,
    onProcessedItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = remember(isGrid) {
        if (isGrid) GridCells.Fixed(2) else GridCells.Fixed(1)
    }
    val horizontalArrangement = remember(isGrid) {
        if (isGrid) Arrangement.spacedBy(16.dp) else Arrangement.Center
    }

    LazyVerticalGrid(
        columns = columns,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = horizontalArrangement,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(
            count = archiveFiles.size,
            key = {
                when (val item = archiveFiles[it]) {
                    is ImazhProcessedFileView -> item.id
                    else -> Unit
                }
            }
        ) { index ->
            when (val item = archiveFiles[index]) {
                is ImazhProcessedFileView -> {
                    ImazhProcessedItem(
                        item = item,
                        isSmallItem = isGrid,
                        imageBuilder = imageBuilder,
                        onClick = { id ->
                            onProcessedItemClick(id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImazhArchiveAppBar(
    isGrid: Boolean,
    showListTypeIcon: Boolean,
    onBackClick: () -> Unit,
    onChangeListTypeClick: () -> Unit,
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
            text = stringResource(id = R.string.lbl_imazh),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        if (showListTypeIcon) {
            IconButton(
                onClick = {
                    safeClick {
                        onChangeListTypeClick()
                    }
                }
            ) {
                ViraIcon(
                    drawable = if (isGrid) {
                        R.drawable.ic_list_column
                    } else {
                        R.drawable.ic_list_grid
                    },
                    contentDescription = stringResource(
                        if (isGrid) {
                            R.string.desc_column
                        } else {
                            R.string.desc_grid
                        }
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
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
                drawable = R.drawable.img_main_page,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_dose_not_exist_any_file),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_make_your_first_file),
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
                drawable = R.drawable.ic_arrow,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )
        }

        Spacer(modifier = Modifier.size(60.dp))
    }
}

@Composable
private fun ImazhArchiveFab(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp
        ),
        visible = isVisible,
        enter = slideInVertically(
            // Enters by sliding down from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 250,
                easing = EaseInOut
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 150,
                easing = EaseInOut
            )
        )
    ) {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                safeClick {
                    onClick()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_add,
                contentDescription = null,
                tint = Color_White
            )
        }
    }
}

@Composable
private fun ImazhProcessedItem(
    item: ImazhProcessedFileView,
    isSmallItem: Boolean,
    imageBuilder: ImageRequest.Builder.(urlPath: String) -> ImageRequest.Builder,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageLoaderState by remember {
        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
    }
    val textPaddingModifier by remember(isSmallItem) {
        mutableStateOf(
            if (isSmallItem) {
                Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            } else {
                Modifier.padding(vertical = 12.dp, horizontal = 22.dp)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                safeClick {
                    onClick(item.id)
                }
            }
    ) {
        if (imageLoaderState is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            )
        }
        ViraAsyncImageUsingCoil(
            imageBuilder = imageBuilder,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            urlPath = item.imagePath,
            onResultCallBack = {
                imageLoaderState = it
            }
        )
        Text(
            text = item.prompt,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = if (isSmallItem) {
                MaterialTheme.typography.body2
            } else {
                MaterialTheme.typography.body1
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color_Blue_Grey_800_945.copy(alpha = 0.75f))
                .then(textPaddingModifier)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        )
    }
}

@Preview
@Composable
fun Preview() {
    ViraPreview {
        ImazhArchiveListScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}

private fun getNewImageResult(navController: NavHostController): Flow<Boolean>? {
    return navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(KEY_NEW_IMAGE_RESULT, false)
        ?.map { it }
}

private fun resetNewImageResult(navController: NavHostController) {
    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.remove<Boolean>(KEY_NEW_IMAGE_RESULT)
}