package ir.part.app.intelligentassistant.ui.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ir.part.app.intelligentassistant.ui.screen.archive.ArchiveProcessedFileElement
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme
import ir.part.app.intelligentassistant.R as AIResource


@Composable
fun AvaNegarSearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val searchText by viewModel.searchText.collectAsState()
    val searchResult by viewModel.getSearchResult.collectAsState()


    AvaNegarSearchBody(
        searchText = searchText,
        searchResult = searchResult,
        arrowForwardAction = {
            navHostController.popBackStack()
        },
        onValueChangeAction = {
            viewModel.onSearchTextChange(it)
        },
        clearState = {
            viewModel.onSearchTextChange("")
        }
    )
}


@Composable
private fun AvaNegarSearchBody(
    modifier: Modifier = Modifier,
    searchText: String,
    searchResult: List<AvanegarProcessedFileView>,
    arrowForwardAction: () -> Unit,
    onValueChangeAction: (String) -> Unit,
    clearState: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                arrowForwardAction()
            }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = ""
                )
            }
            TextField(
                shape = RoundedCornerShape(0.dp),
                value = searchText,
                onValueChange = { onValueChangeAction(it) },
                placeholder = {
                    Text(stringResource(id = AIResource.string.lbl_search_in_archive))
                },
                leadingIcon = {
                    Image(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = ""
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        clearState()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = ""
                        )
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = searchResult,
            ) { item ->
                ArchiveProcessedFileElement(
                    archiveViewProcessed = item,
                    onItemClick = {},
                    onMenuClick = {}
                )
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun AvaNegarSearchScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvaNegarSearchScreen(navHostController = rememberNavController())
        }
    }
}