package ai.ivira.app.features.home.ui.onboarding

import ai.ivira.app.R
import android.content.Context
import androidx.annotation.DrawableRes

sealed class MainOnboardingItem(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: List<String>
) {
    data class First(private val context: Context) : MainOnboardingItem(
        image = R.drawable.img_mic_text,
        title = R.string.lbl_avanegar_service,
        description = listOf(context.getString(R.string.lbl_avanegar_service_details))
    )

    data class Second(private val context: Context) : MainOnboardingItem(
        image = R.drawable.img_mic_text_grammer_1,
        title = R.string.lbl_convert_text_to_speech_service,
        description = listOf(context.getString(R.string.lbl_convert_text_to_speech_service_details))
    )

    data class Third(private val context: Context) : MainOnboardingItem(
        image = R.drawable.img_ai_tools,
        title = R.string.lbl_soon_in_vira,
        description = buildList {
            add(context.getString(R.string.lbl_soon_in_vira_description_first))
            add(context.getString(R.string.lbl_soon_in_vira_description_second))
            add(context.getString(R.string.lbl_soon_in_vira_description_third))
        }
    )
}