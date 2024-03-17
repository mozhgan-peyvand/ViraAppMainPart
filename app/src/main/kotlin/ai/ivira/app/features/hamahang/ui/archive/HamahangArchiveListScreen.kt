package ai.ivira.app.features.hamahang.ui.archive

import ai.ivira.app.R
import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
import ai.ivira.app.utils.ui.isScrollingUp
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HamahangArchiveListScreenRoute(navController: NavController) {
    HamahangArchiveListScreen(
        navigateToDetailScreen = { id ->
            navController.navigate(HamahangScreenRoutes.HamahangDetailScreen.createRoute(id))
        },
        navigateToNewAudio = {
            navController.navigate(HamahangScreenRoutes.HamahangNewAudioScreen.route)
        },
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun HamahangArchiveListScreen(
    navigateToDetailScreen: (id: Int) -> Unit,
    navigateToNewAudio: () -> Unit,
    navigateUp: () -> Unit
) {
    val hamahangListState by rememberLazyListState().isScrollingUp()

    HamahangArchiveListUI(
        navigateToNewAudio = navigateToNewAudio,
        navigateUp = navigateUp,
        isVisible = hamahangListState
    )
}

@Composable
private fun HamahangArchiveListUI(
    isVisible: Boolean,
    navigateToNewAudio: () -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.background(Color_BG),
        topBar = {
            HamahangArchiveAppBar(onBackClick = navigateUp)
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            HamahangArchiveEmptyBody()
            HamahangFab(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = navigateToNewAudio,
                isVisible = isVisible
            )
        }
    }
}

@Composable
private fun HamahangArchiveEmptyBody(
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
                text = stringResource(id = R.string.lbl_dose_not_exist_any_file_yet),
                style = MaterialTheme.typography.subtitle1,
                color = Color_Text_1,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.lbl_make_your_first_audio_file),
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
private fun HamahangFab(
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
private fun HamahangArchiveAppBar(
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
            text = stringResource(id = R.string.lbl_hamahang),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}