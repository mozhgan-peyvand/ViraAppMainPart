package ai.ivira.app.features.home.ui.about_us

import ai.ivira.app.BuildConfig
import ai.ivira.app.R
import ai.ivira.app.features.home.ui.HomeAnalytics
import ai.ivira.app.utils.ui.analytics.LocalEventHandler
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_2
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.widgets.ViraIcon
import ai.ivira.app.utils.ui.widgets.ViraImage
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AboutUsScreenRoute(navController: NavController) {
    val eventHandler = LocalEventHandler.current
    LaunchedEffect(Unit) {
        eventHandler.screenViewEvent(HomeAnalytics.screenViewAboutUs)
    }

    AboutUsScreen(navController = navController)
}

@Composable
private fun AboutUsScreen(navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color_BG)
    ) {
        AboutUsTopAppBar(
            onBackClick = { navController.navigateUp() }
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            ViraImage(
                drawable = R.drawable.ic_blue_logo,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(81.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            ViraImage(
                drawable = R.drawable.ic_blue_vira,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(width = 135.dp, height = 40.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = buildString {
                    append(stringResource(id = R.string.lbl_version)).append(
                        BuildConfig.VERSION_NAME
                    )
                },
                color = Color_Primary_200,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.size(32.dp))

            Text(
                text = stringResource(id = R.string.desc_about_us),
                color = Color_Text_2,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 18.dp
                )
            )

            Spacer(modifier = Modifier.size(16.dp))

            TextButton(
                border = BorderStroke(1.dp, Color_OutLine),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = 20.dp,
                    end = 16.dp
                ),
                onClick = {
                    safeClick {
                        kotlin.runCatching {
                            val intent = Intent(Intent.ACTION_SENDTO)
                            intent.data =
                                Uri.parse("mailto:${context.getString(R.string.lbl_email)}")
                            context.startActivity(intent)
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lbl_email),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Primary_200
                )

                Spacer(modifier = Modifier.size(10.dp))

                ViraIcon(
                    drawable = R.drawable.ic_email,
                    contentDescription = null,
                    tint = Color_Primary_200
                )
            }
        }
    }
}

@Composable
fun AboutUsTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(onClick = {
            safeClick {
                onBackClick()
            }
        }) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = Color_White
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_about_vira),
            color = Color_White
        )
    }
}

@Preview
@Composable
private fun ArchiveEmptyBodyPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            AboutUsScreen(rememberNavController())
        }
    }
}