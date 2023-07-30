package ir.part.app.intelligentassistant.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.part.app.intelligentassistant.ui.theme.Gray_200
import ir.part.app.intelligentassistant.ui.theme.IntelligentAssistantTheme
import kotlinx.coroutines.launch
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun HomeScreen() {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeTopAppBar() {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        }, drawerContent = {
            DrawerHeader({}, {})

        }, drawerGesturesEnabled = scaffoldState.drawerState.isOpen

    ) { innerPadding ->
        HomeBody(innerPadding)
    }


}

@Composable
private fun HomeBody(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(paddingValues)
            .verticalScroll(
                scrollState
            )
    ) {
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_ava_negar),
            description = stringResource(id = AIResource.string.lbl_change_voice_to_text),
            showNextStepIcon = true
        )
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_ava_sho),
            description = stringResource(id = AIResource.string.coming_soon)
        )
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_vira_part),
            description = stringResource(id = AIResource.string.coming_soon)
        )
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_nevise_negar),
            description = stringResource(id = AIResource.string.coming_soon)
        )
    }
}

@Composable
fun HomeBodyItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    showNextStepIcon: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp, vertical = 10.dp
            ), shape = RoundedCornerShape(8.dp),
        backgroundColor = Gray_200
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    Modifier.padding(10.dp),
                    fontSize = 25.sp
                )
                Text(text = description, Modifier.padding(10.dp))
            }
            if (showNextStepIcon) {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowLeft,
                        contentDescription = "arrowForward"
                    )
                }
            }
        }

    }

}

@Composable
private fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = { onNavigationClick() },
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "menu", Modifier
            )
        }
        Text(
            text = stringResource(id = AIResource.string.app_name),
            Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    IntelligentAssistantTheme {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
        ) {
            HomeScreen()

        }
    }
}




