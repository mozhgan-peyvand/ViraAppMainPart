package ai.ivira.app.features.ava_negar.ui.search

import ai.ivira.app.R
import ai.ivira.app.features.ava_negar.ui.SnackBar
import ai.ivira.app.features.ava_negar.ui.archive.model.AvanegarProcessedFileView
import ai.ivira.app.features.ava_negar.ui.search.element.SearchFileElementGrid
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AvaNegarSearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackBar(it)
        }
    ) { innerPadding ->
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

@Composable
private fun AvaNegarSearchBody(
    modifier: Modifier = Modifier,
    searchText: String,
    searchResult: List<AvanegarProcessedFileView>,
    focusRequester: FocusRequester,
    arrowForwardAction: () -> Unit,
    onValueChangeAction: (String) -> Unit,
    clearState: () -> Unit,
    isSearch: Boolean,
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
                        items = searchResult
                    ) { item ->
                        SearchFileElementGrid(
                            archiveViewProcessed = item,
                            onItemClick = { onItemClick(it) }
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
                top = 8.dp,
                bottom = 8.dp,
                end = 16.dp,
                start = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            safeClick {
                arrowForwardAction()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_right,
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
                ViraImage(
                    drawable = R.drawable.ic_search_n,
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
                    ViraIcon(
                        drawable = R.drawable.ic_clear,
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
            )
        )
    }
}