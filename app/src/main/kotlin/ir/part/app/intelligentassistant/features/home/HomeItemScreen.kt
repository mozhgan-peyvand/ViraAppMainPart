package ir.part.app.intelligentassistant.features.home


data class HomeItemScreen(
    val icon: Int,
    val title: Int,
    val description: Int,
    val onItemClick: () -> Unit
)