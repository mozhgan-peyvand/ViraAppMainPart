package ai.ivira.app.features.ava_negar.ui.onboarding

import ai.ivira.app.R
import android.content.Context
import androidx.annotation.DrawableRes

sealed class OnboardingItem(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: List<String>
) {
    data class First(val context: Context) : OnboardingItem(
        image = R.drawable.img_ai_sound_to_text,
        title = context.getString(R.string.lbl_avanegar_onboarding_first_title),
        description = listOf(context.getString(R.string.lbl_avanegar_onboarding_first_description))
    )

    data class Second(val context: Context) : OnboardingItem(
        image = R.drawable.img_mic_file,
        title = context.getString(R.string.lbl_avanegar_onboarding_second_title),
        description = buildList {
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_first))
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_second))
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_third))
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_fourth))
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_fifth))
            add(context.getString(R.string.lbl_avanegar_onboarding_second_description_sixth))
        }
    )

    data class Third(val context: Context) : OnboardingItem(
        image = R.drawable.img_mic_text_grammer,
        title = context.getString(R.string.lbl_avanegar_onboarding_third_title),
        description = buildList {
            add(context.getString(R.string.lbl_avanegar_onboarding_third_description_first))
            add(context.getString(R.string.lbl_avanegar_onboarding_third_description_second))
            add(context.getString(R.string.lbl_avanegar_onboarding_third_description_third))
            add(context.getString(R.string.lbl_avanegar_onboarding_third_description_fourth))
            add(context.getString(R.string.lbl_avanegar_onboarding_third_description_fifth))
        }
    )
}