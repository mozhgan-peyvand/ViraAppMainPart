package ai.ivira.app.features.avasho.ui.archive

import ai.ivira.app.R
import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.avasho.ui.file_creation.SpeechResult
import ai.ivira.app.utils.ui.navigation.ScreenRoutes
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AvashoArchiveListScreen(
    navController: NavHostController,
    viewModel: AvashoArchiveListViewModel = hiltViewModel()
) {
    navController.currentBackStackEntry
        ?.savedStateHandle?.remove<SpeechResult>(SpeechResult.FILE_NAME)?.let {
            viewModel.getSpeechFile(
                fileName = it.fileName,
                speakerType = it.speakerType,
                text = it.speakerType
            )
        }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            ArchiveAppBar(onBackClick = { navController.navigateUp() })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ArchiveEmptyBody()
            Fabs(
                modifier = Modifier.align(Alignment.BottomStart),
                onMainFabClick = {
                    navController.navigate(route = ScreenRoutes.AvaShoFileCreationScreen.route)
                }
            )
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
                drawable = drawable.img_main_page,
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
private fun Fabs(
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

@Preview(showBackground = true, backgroundColor = 0xFF070707)
@Composable
private fun AvashoArchiveListScreenPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AvashoArchiveListScreen(navController = rememberNavController())
        }
    }
}