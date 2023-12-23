package ai.ivira.app.features.avasho.ui.onboarding

import ai.ivira.app.R
import android.content.Context
import androidx.annotation.DrawableRes

sealed class AvashoOnboardingItem(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: List<String>
) {
    data class First(val context: Context) : AvashoOnboardingItem(
        image = R.drawable.img_ai_human,
        title = context.getString(R.string.lbl_avasho_onboarding_first_title),
        description = listOf(context.getString(R.string.lbl_avasho_onboarding_first_description))
    )

    data class Second(val context: Context) : AvashoOnboardingItem(
        image = R.drawable.img_text_to_speech,
        title = context.getString(R.string.lbl_avasho_onboarding_second_title),
        description = buildList {
            add(context.getString(R.string.lbl_avasho_onboarding_second_description_first))
            add(context.getString(R.string.lbl_avasho_onboarding_second_description_second))
            add(context.getString(R.string.lbl_avasho_onboarding_second_description_third))
            add(context.getString(R.string.lbl_avasho_onboarding_second_description_fourth))
            add(context.getString(R.string.lbl_avasho_onboarding_second_description_fifth))
        }
    )
}