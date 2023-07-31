package ir.part.app.intelligentassistant.ui.screen.archive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.ui.navigation.ScreensRouter
import ir.part.app.intelligentassistant.ui.screen.archive.entity.ArchiveView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarProcessedFileView
import ir.part.app.intelligentassistant.ui.screen.archive.entity.AvanegarTrackingFileView
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme

@Composable
fun AvaNegarArchiveScreen(
    archiveViewModel: AvaNegarArchiveViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    var isFabExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ArchiveAppBar(
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(if (isFabExpanded) 0.3f else 0.9f),
            isLock = !isFabExpanded,
            onBackClick = {
                navHostController.popBackStack()
            },
            onSearchClick = { navHostController.navigate(ScreensRouter.AvaNegarSearchScreen.router) }
        )

        Box(modifier = Modifier.weight(1f)) {

            if (archiveViewModel.allArchiveFiles.value.isEmpty()) {
                ArchiveBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isFabExpanded) 0.3f else 0.9f)
                )
            } else {
                ArchiveList(
                    list = archiveViewModel.allArchiveFiles.value,
                    isLock = isFabExpanded,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .alpha(if (isFabExpanded) 0.3f else 0.9f)
                        .pointerInput(Unit) {}
                )
            }

            Fabs(
                isFabExpanded = isFabExpanded,
                modifier = Modifier
                    .align(Alignment.BottomStart),
                onMainFabClick = { isFabExpanded = !isFabExpanded }
            )

        }
    }
}

@Composable
private fun ArchiveAppBar(
    modifier: Modifier = Modifier,
    isLock: Boolean,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            enabled = isLock,
            onClick = { onBackClick() },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.lbl_ava_negar),
            Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        IconButton(
            enabled = isLock,
            onClick = onSearchClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        }

    }

}

@Composable
private fun ArchiveBody(
    modifier: Modifier = Modifier
) {

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.ic_image_default),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.lbl_dont_have_file),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.lbl_make_your_first_file),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.img_arrow),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(100.dp))
    }

}

@Composable
fun Fabs(
    modifier: Modifier = Modifier,
    isFabExpanded: Boolean,
    onMainFabClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
    ) {

        AnimatedVisibility(visible = isFabExpanded) {

            FloatingActionButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp),
                onClick = {
                    //TODO implement onCLick
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = isFabExpanded) {

            FloatingActionButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(bottom = 8.dp),
                onClick = {
                    //TODO implement onCLick
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic),
                    contentDescription = null
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .clip(CircleShape),
            onClick = onMainFabClick
        ) {
            Icon(
                painter = painterResource(id = if (isFabExpanded) R.drawable.ic_close else R.drawable.ic_add),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ArchiveList(
    list: List<ArchiveView>,
    isLock: Boolean,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        userScrollEnabled = !isLock,
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = list,
            key = { item ->
                when (item) {
                    is AvanegarProcessedFileView -> item.id

                    is AvanegarTrackingFileView -> item.token
                    else -> {}
                }
            }) {

            when (it) {
                is AvanegarProcessedFileView -> {
                    ArchiveProcessedFileElement(
                        archiveViewProcessed = it,
                        isLock = isLock,
                        onItemClick = {},
                        onMenuClick = {}
                    )

                }

                is AvanegarTrackingFileView -> {
                    ArchiveTrackingFileElements(
                        archiveTrackingView = it,
                        isLock = isLock,
                        onItemClick = {},
                        onTryAgainButtonClick = {}
                    )

                }
            }
        }
    }
}

@Composable
private fun ArchiveProcessedFileElement(
    archiveViewProcessed: AvanegarProcessedFileView,
    isLock: Boolean,
    onItemClick: (Int) -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        backgroundColor = if (archiveViewProcessed.isSeen) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp),
        enabled = !isLock,
        onClick = { onItemClick(archiveViewProcessed.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically),
                    text = archiveViewProcessed.title
                )

                IconButton(
                    modifier = Modifier.clickable { !isLock },
                    onClick = onMenuClick,
                    enabled = !isLock
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dots_menu),
                        contentDescription = null
                    )
                }
            }



            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = archiveViewProcessed.text
            )


            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = archiveViewProcessed.createdAt
            )
        }
    }
}

@Composable
private fun ArchiveTrackingFileElements(
    archiveTrackingView: AvanegarTrackingFileView,
    isLock: Boolean,
    onItemClick: (String) -> Unit,
    onTryAgainButtonClick: (String) -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(16.dp),
        enabled = !isLock,
        onClick = { onItemClick(archiveTrackingView.token) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(128.dp)
        ) {

            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = archiveTrackingView.title
            )

            //TODO Remove it
            Button(
                onClick = { onTryAgainButtonClick(archiveTrackingView.token) },
                enabled = !isLock
            ) {
                Text(text = "تلاش مجدد")
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.lbl_converting)
            )
        }
    }
}

@Preview
@Composable
fun ArchiveListPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveList(listOf(), false)
        }
    }
}

@Preview
@Composable
fun ArchiveElementProcessedFilePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveProcessedFileElement(
                archiveViewProcessed = AvanegarProcessedFileView(
                    0,
                    "title",
                    "text",
                    "0",
                    "",
                    false
                ),
                isLock = false,
                onItemClick = {},
                onMenuClick = {}
            )
        }
    }
}


@Preview
@Composable
fun ArchiveElementTrackingFilePreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArchiveTrackingFileElements(
                archiveTrackingView = AvanegarTrackingFileView(
                    "",
                    "",
                    "title",
                    "0"
                ),
                isLock = false,
                onItemClick = {},
                onTryAgainButtonClick = {}
            )
        }
    }
}

@Preview
@Composable
fun AvaNegarArchiveScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvaNegarArchiveScreen(navHostController = rememberNavController())
        }
    }
}