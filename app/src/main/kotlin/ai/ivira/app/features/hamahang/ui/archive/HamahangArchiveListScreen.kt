package ai.ivira.app.features.hamahang.ui.archive

import ai.ivira.app.features.hamahang.ui.HamahangScreenRoutes
import androidx.compose.runtime.Composable
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
    navigateToDetailScreen: (id: String) -> Unit,
    navigateToNewAudio: () -> Unit,
    navigateUp: () -> Unit
) {
    HamahangArchiveListUI()
}

@Composable
private fun HamahangArchiveListUI() {
}