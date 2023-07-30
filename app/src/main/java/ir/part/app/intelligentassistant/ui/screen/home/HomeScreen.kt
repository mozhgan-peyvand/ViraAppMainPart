package ir.part.app.intelligentassistant.ui.screen.home

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.ui.theme.Gray_200
import kotlinx.coroutines.launch
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
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
        HomeBody(
            innerPadding,
            homeViewModel = homeViewModel,
            navController = navController
        )
    }


}

@Composable
private fun HomeBody(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
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
        ) {
            // navigate to ava negar
            val screen by homeViewModel.startDestination
            navController.navigate(screen)

        }
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_ava_sho),
            description = stringResource(id = AIResource.string.coming_soon)
        ) {

        }
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_vira_part),
            description = stringResource(id = AIResource.string.coming_soon)
        ) {

        }
        HomeBodyItem(
            title = stringResource(id = AIResource.string.lbl_nevise_negar),
            description = stringResource(id = AIResource.string.coming_soon)
        ) {

        }
    }
}

@Composable
fun HomeBodyItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    showNextStepIcon: Boolean = false, onCardClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp, vertical = 10.dp
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(8.dp),
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





