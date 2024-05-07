package ai.ivira.app.features.home.ui.terms

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.features.home.ui.terms.TermsOfServiceView.Companion.descriptionList
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_About_Us_Background_Gradiant_20
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_Transparent
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavController

@Composable
fun TermsOfServicesScreenRoute(navController: NavController) {
    TermsOfServicesScreen(
        navigateUp = { navController.navigateUp() }
    )
}

@Composable
private fun TermsOfServicesScreen(
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val startYGradiantSize = with(LocalDensity.current) {
        100.dp.toPx()
    }

    val endYGradiantSize = with(LocalDensity.current) {
        900.dp.toPx()
    }

    val gradientBrush by remember(startYGradiantSize, endYGradiantSize) {
        mutableStateOf(
            Brush.verticalGradient(
                colors = listOf(
                    Color_Transparent,
                    Color_About_Us_Background_Gradiant_20,
                    Color_Transparent
                ),
                tileMode = TileMode.Clamp,
                startY = startYGradiantSize,
                endY = endYGradiantSize
            )
        )
    }

    val scrollState = rememberScrollState(0)

    val scrollStateValue by remember(scrollState.value) {
        mutableStateOf(Dp((scrollState.value - (scrollState.value * 0.55)).toFloat()))
    }

    val offsetX by remember(scrollStateValue) {
        mutableStateOf(110.dp + scrollStateValue)
    }

    val offsetY by remember(scrollStateValue) {
        mutableStateOf((-70).dp - scrollStateValue)
    }

    DisposableEffect(Unit) {
        val activity = context as ComponentActivity
        val color = activity.window.statusBarColor
        val window = activity.window
        val isAndroid11OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

        if (isAndroid11OrAbove) {
            window.setDecorFitsSystemWindows(false)
            activity.window.statusBarColor = Color_Transparent.toArgb()
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        onDispose {
            if (isAndroid11OrAbove) {
                window.setDecorFitsSystemWindows(true)
                activity.window.statusBarColor = color
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
            .padding(top = 24.dp) // Because edgeToEdge is enabled, we set it manually
    ) {
        ViraImage(
            drawable = R.drawable.ic_blue_logo,
            contentDescription = null,
            alpha = 0.2f,
            modifier = Modifier
                .size(262.dp)
                .align(Alignment.TopEnd)
                .offset(offsetX, offsetY)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(onBackClick = navigateUp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .drawWithContent {
                        drawRect(gradientBrush)
                        drawContent()
                    }
            ) {
                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp)
                    ) {
                        ViraImage(
                            drawable = R.drawable.ic_blue_vira,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .size(width = 135.dp, height = 40.dp)
                        )

                        Spacer(modifier = Modifier.size(4.dp))

                        Text(
                            text = buildString {
                                append(stringResource(id = R.string.lbl_version)).append(
                                    BuildConfig.VERSION_NAME
                                )
                            },
                            color = Color_Primary_200,
                            style = MaterialTheme.typography.body2
                        )

                        Spacer(modifier = Modifier.size(56.dp))

                        descriptionList.fastForEach { termsOfService ->

                            Text(
                                text = stringResource(id = termsOfService.header),
                                color = Color_Text_2,
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )

                            termsOfService.description.fastForEach { description ->
                                Text(
                                    text = stringResource(id = description),
                                    color = Color_Text_2,
                                    style = MaterialTheme.typography.body2,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = {
                safeClick { onBackClick() }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = Color_White
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_terms_rules),
            color = Color_White,
            style = MaterialTheme.typography.subtitle2
        )
    }
}

@ViraDarkPreview
@Composable
private fun TermsOfServicesScreenPreview() {
    ViraPreview {
        TermsOfServicesScreen(
            navigateUp = {}
        )
    }
}