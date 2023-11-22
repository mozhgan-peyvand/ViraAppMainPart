package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R.drawable
import ai.ivira.app.R.string
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.AvaSho
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NeviseNama
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NeviseNegar
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.ViraSiar
import ai.ivira.app.utils.ui.theme.Indigo_300
import ai.ivira.app.utils.ui.theme.Indigo_300_2
import ai.ivira.app.utils.ui.theme.Light_green_300
import ai.ivira.app.utils.ui.theme.Teal_200
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

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
                        icon = drawable.img_ava_sho_2,
                        title = string.lbl_ava_sho,
                        textColor = Indigo_300,
                        description = string.lbl_ava_sho_desc,
                        homeItemType = AvaSho
                    ),
                    HomeItemScreen(
                        icon = drawable.img_nevise_negar_2,
                        textColor = Indigo_300_2,
                        title = string.lbl_nevise_negar,
                        description = string.lbl_nevise_negar_desc,
                        homeItemType = NeviseNegar
                    ),
                    HomeItemScreen(
                        icon = drawable.img_vira_siar_2,
                        title = string.lbl_virasiar,
                        textColor = Light_green_300,
                        description = string.lbl_virasiar_desc,
                        homeItemType = ViraSiar
                    ),
                    HomeItemScreen(
                        icon = drawable.img_nevise_nama_2,
                        title = string.lbl_nevise_nama,
                        textColor = Teal_200,
                        description = string.lbl_nevise_nama_desc,
                        homeItemType = NeviseNama
                    )
                )
            }
    }
}