package ir.part.app.intelligentassistant.features.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ir.part.app.intelligentassistant.features.home.HomeItemScreen
import ir.part.app.intelligentassistant.utils.ui.navigation.ScreenRoutes
import ir.part.app.intelligentassistant.utils.ui.theme.Blue_Grey_900
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Card_Stroke
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_1
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_2
import ir.part.app.intelligentassistant.utils.ui.theme.Color_Text_3
import ir.part.app.intelligentassistant.utils.ui.theme.Indigo_300
import ir.part.app.intelligentassistant.utils.ui.theme.Light_blue_50
import ir.part.app.intelligentassistant.utils.ui.theme.labelMedium
import ir.part.app.intelligentassistant.R as AIResource

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {

    LaunchedEffect(homeViewModel.onboardingHasBeenShown.value, homeViewModel.shouldNavigate.value) {

        if (homeViewModel.shouldNavigate.value) {
            if (!homeViewModel.onboardingHasBeenShown.value)
                navController.navigate(ScreenRoutes.AvaNegarOnboarding.route)
            else
                navController.navigate(ScreenRoutes.AvaNegarArchiveList.route)

            homeViewModel.shouldNavigate.value = false
        }
    }

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState
    ) { innerPadding ->
        HomeBody(
            paddingValues = innerPadding,
            onAvaneagrClick = {
                // navigate to ava negar
                homeViewModel.navigate()
            }
        )
    }
}

@Composable
private fun HomeBody(
    paddingValues: PaddingValues,
    onAvaneagrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeItem = remember {
        mutableListOf(
            HomeItemScreen(
                icon = AIResource.drawable.img_ava_sho,
                title = AIResource.string.lbl_ava_sho,
                description = AIResource.string.lbl_ava_sho_desc
            ) {},
            HomeItemScreen(
                icon = AIResource.drawable.img_nevise_nama,
                title = AIResource.string.lbl_nevise_nama,
                description = AIResource.string.lbl_nevise_negar_desc
            ) {},
            HomeItemScreen(
                icon = AIResource.drawable.img_vira_part,
                title = AIResource.string.lbl_vira_part,
                description = AIResource.string.lbl_vira_part_desc
            ) {},
            HomeItemScreen(
                icon = AIResource.drawable.img_nevise_nama_2,
                title = AIResource.string.lbl_nevise_nama,
                description = AIResource.string.lbl_nevise_nama_desc
            ) {}
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .paint(
                painterResource(id = AIResource.drawable.bg_pattern),
                contentScale = ContentScale.Crop
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = AIResource.drawable.ic_vira),
                contentDescription = null
            )
            Text(
                text = stringResource(id = AIResource.string.app_name),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.h6,
                color = Color_Text_1
            )
        }
        Text(
            text = stringResource(id = AIResource.string.lbl_assistant),
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.body2,
            color = Color_Text_2
        )
        Card(
            modifier = modifier
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                onAvaneagrClick()
            }
        ) {
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(
                        top = 18.dp,
                        bottom = 18.dp,
                        start = 30.dp,
                        end = 18.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = AIResource.drawable.img_voice),
                    contentDescription = "",
                    modifier = Modifier.size(
                        width = 68.dp,
                        height = 80.dp
                    )
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = AIResource.string.lbl_ava_negar),
                        style = MaterialTheme.typography.h6,
                        color = Color_Text_1
                    )
                    Text(
                        text = stringResource(id = AIResource.string.lbl_ava_negar_desc),
                        color = Light_blue_50,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp)
                        .background(
                            Color.Black
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = AIResource.drawable.ic_arrow_crooked),
                        contentDescription = "ic_arrow"
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = homeItem,
            ) { item ->
                HomeBodyItem(item = item)
            }

        }
    }
}

@Composable
fun HomeBodyItem(item: HomeItemScreen) {
    Box(
        modifier = Modifier
            .aspectRatio(0.95f)
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.TopCenter,
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            border = BorderStroke(0.5.dp, color = Color_Card_Stroke),
            backgroundColor = Color_Card
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = item.title),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color_Text_1
                )
                Text(
                    text = stringResource(id = item.description),
                    style = MaterialTheme.typography.labelMedium,
                    color = Indigo_300
                )

                Surface(
                    shape = CircleShape,
                    border = BorderStroke(
                        0.5.dp,
                        color = Color_Card_Stroke
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = AIResource.string.lbl_coming_soon),
                        modifier = Modifier
                            .background(Blue_Grey_900)
                            .padding(
                                horizontal = 17.dp,
                                vertical = 10.dp
                            ),
                        style = MaterialTheme.typography.overline,
                        color = Color_Text_3
                    )
                }
            }
        }
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .shadow(4.dp)
        )
    }
}
