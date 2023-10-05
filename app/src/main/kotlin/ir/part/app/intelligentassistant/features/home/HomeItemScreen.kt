package ir.part.app.intelligentassistant.features.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ir.part.app.intelligentassistant.R
import ir.part.app.intelligentassistant.features.home.ui.HomeItemBottomSheetType
import ir.part.app.intelligentassistant.utils.ui.theme.Indigo_300
import ir.part.app.intelligentassistant.utils.ui.theme.Indigo_300_2
import ir.part.app.intelligentassistant.utils.ui.theme.Light_green_300
import ir.part.app.intelligentassistant.utils.ui.theme.Teal_200

data class HomeItemScreen(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val textColor: Color,
    @StringRes val description: Int,
    val homeItemType: HomeItemBottomSheetType
) {
    companion object {
        val items: List<HomeItemScreen>
            get() {
                return listOf(
                    HomeItemScreen(
                        icon = R.drawable.img_ava_sho,
                        title = R.string.lbl_ava_sho,
                        textColor = Indigo_300,
                        description = R.string.lbl_ava_sho_desc,
                        homeItemType = HomeItemBottomSheetType.AvaSho
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_nevise_negar,
                        textColor = Indigo_300_2,
                        title = R.string.lbl_nevise_negar,
                        description = R.string.lbl_nevise_negar_desc,
                        homeItemType = HomeItemBottomSheetType.NeviseNegar
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_virasiar,
                        title = R.string.lbl_virasiar,
                        textColor = Light_green_300,
                        description = R.string.lbl_virasiar_desc,
                        homeItemType = HomeItemBottomSheetType.ViraSiar
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_nevise_nama,
                        title = R.string.lbl_nevise_nama,
                        textColor = Teal_200,
                        description = R.string.lbl_nevise_nama_desc,
                        homeItemType = HomeItemBottomSheetType.NeviseNama
                    )
                )
            }
    }
}