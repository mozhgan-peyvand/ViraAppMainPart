package ir.part.app.intelligentassistant.features.home

import androidx.compose.ui.graphics.Color
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.utils.ui.theme.Indigo_300
import ir.part.app.intelligentassistant.utils.ui.theme.Indigo_300_2
import ir.part.app.intelligentassistant.utils.ui.theme.Light_green_300
import ir.part.app.intelligentassistant.utils.ui.theme.Teal_200


data class HomeItemScreen(
    val icon: Int,
    val title: Int,
    val textColor: Color,
    val description: Int
) {

    companion object {
        val items: List<HomeItemScreen>
            get() {
                return listOf(
                    HomeItemScreen(
                        icon = R.drawable.img_ava_sho,
                        title = R.string.lbl_ava_sho,
                        textColor = Indigo_300,
                        description = R.string.lbl_ava_sho_desc
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_nevise_negar,
                        textColor = Indigo_300_2,
                        title = R.string.lbl_nevise_negar,
                        description = R.string.lbl_nevise_negar_desc
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_virasiar,
                        title = R.string.lbl_virasiar,
                        textColor = Light_green_300,
                        description = R.string.lbl_virasiar_desc
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_nevise_nama,
                        title = R.string.lbl_nevise_nama,
                        textColor = Teal_200,
                        description = R.string.lbl_nevise_nama_desc
                    )
                )
            }
    }
}