package ai.ivira.app.features.home.ui.home

import ai.ivira.app.R
import ai.ivira.app.utils.ui.theme.Indigo_100
import ai.ivira.app.utils.ui.theme.Indigo_300
import ai.ivira.app.utils.ui.theme.Pink_100
import ai.ivira.app.utils.ui.theme.Red_20
import ai.ivira.app.utils.ui.theme.teal_100
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class HomeItemScreen(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val textColor: Color,
    @StringRes val description: Int,
    @DrawableRes val banner: Int,
    val isComingSoon: Boolean,
    val homeItemType: HomeItemType
) {
    companion object {
        private val avaNegar = HomeItemScreen(
            icon = R.drawable.img_ava_negar,
            title = R.string.lbl_ava_negar,
            textColor = Indigo_100,
            description = R.string.lbl_ava_negar_desc,
            banner = R.drawable.img_banner_avanegar,
            isComingSoon = false,
            homeItemType = HomeItemType.Avanegar
        )
        private val avaSho = HomeItemScreen(
            icon = R.drawable.img_ava_sho,
            title = R.string.lbl_ava_sho,
            textColor = Indigo_300,
            description = R.string.lbl_ava_sho_desc,
            banner = R.drawable.img_banner_avasho,
            isComingSoon = false,
            homeItemType = HomeItemType.Avasho
        )
        private val imazh = HomeItemScreen(
            icon = R.drawable.img_imazh,
            title = R.string.lbl_imazh,
            textColor = Red_20,
            description = R.string.lbl_imazh_desc,
            banner = R.drawable.img_banner_imazh,
            isComingSoon = false,
            homeItemType = HomeItemType.Imazh
        )

        val hamahang = HomeItemScreen(
            icon = R.drawable.img_hamahang,
            textColor = Pink_100,
            title = R.string.lbl_hamahang,
            description = R.string.lbl_sound_imitation,
            banner = R.drawable.img_banner_hamahang,
            isComingSoon = false,
            homeItemType = HomeItemType.Hamahang
        )

        val chatGpt = HomeItemScreen(
            icon = R.drawable.img_chatgpt,
            textColor = teal_100,
            title = R.string.lbl_chatgpt,
            description = R.string.lbl_chatgpt_desc,
            banner = 0,
            isComingSoon = true,
            homeItemType = HomeItemType.ChatGpt
        )

        val mainItemList: List<HomeItemScreen>
            get() {
                return listOf(avaNegar, avaSho, imazh, hamahang, chatGpt)
            }

        val bannerItemList: List<HomeItemScreen>
            get() {
                return listOf(hamahang, imazh, avaNegar, avaSho)
            }
    }
}

enum class HomeItemType {
    Avanegar,
    Avasho,
    Imazh,
    Hamahang,
    ChatGpt
}