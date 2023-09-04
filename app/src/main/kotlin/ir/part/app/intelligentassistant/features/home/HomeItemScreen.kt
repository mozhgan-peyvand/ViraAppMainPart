package ir.part.app.intelligentassistant.features.home

import androidx.compose.ui.graphics.Color


data class HomeItemScreen(
    val icon: Int,
    val title: Int,
    val textColor: Color,
    val description: Int,
    val onItemClick: () -> Unit
)