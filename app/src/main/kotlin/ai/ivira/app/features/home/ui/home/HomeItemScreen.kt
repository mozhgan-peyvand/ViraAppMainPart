package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.Imazh
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.NeviseNegar
import ai.ivira.app.features.home.ui.home.sheets.HomeItemBottomSheetType.ViraSiar
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
                        icon = R.drawable.img_imazh,
                        title = R.string.lbl_imazh,
                        textColor = Teal_200,
                        description = R.string.lbl_imazh_desc,
                        homeItemType = Imazh
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_nevise_negar_2,
                        textColor = Indigo_300_2,
                        title = R.string.lbl_nevise_negar,
                        description = R.string.lbl_nevise_negar_desc,
                        homeItemType = NeviseNegar
                    ),
                    HomeItemScreen(
                        icon = R.drawable.img_vira_siar_2,
                        title = R.string.lbl_virasiar,
                        textColor = Light_green_300,
                        description = R.string.lbl_virasiar_desc,
                        homeItemType = ViraSiar
                    )
                )
            }
    }
}