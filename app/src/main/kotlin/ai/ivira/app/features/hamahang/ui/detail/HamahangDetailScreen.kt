package ai.ivira.app.features.hamahang.ui.detail

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HamahangDetailScreenRoute(navController: NavController) {
    HamahangDetailScreen(
        navigateUp = {
            navController.navigateUp()
        }
    )
}

@Composable
private fun HamahangDetailScreen(navigateUp: () -> Unit) {
    HamahangDetailUI()
}

@Composable
private fun HamahangDetailUI() {
}