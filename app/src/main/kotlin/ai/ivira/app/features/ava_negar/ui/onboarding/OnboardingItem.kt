package ai.ivira.app.features.ava_negar.ui.onboarding

import ai.ivira.app.R
import ai.ivira.app.features.home.onboarding.addBullet
import android.content.Context
import androidx.annotation.DrawableRes

sealed class OnboardingItem(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    data class First(val context: Context) : OnboardingItem(
        image = R.drawable.img_ai_sound_to_text,
        title = context.getString(R.string.lbl_avanegar_onboarding_first_title),
        description = context.getString(R.string.lbl_avanegar_onboarding_first_description)
    )

    data class Second(val context: Context) : OnboardingItem(
        image = R.drawable.img_mic_file,
        title = context.getString(R.string.lbl_avanegar_onboarding_second_title),
        description = buildString {
            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_first
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_second
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_third
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_fourth
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_fifth
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_second_description_sixth
                ).addBullet()
            )
        }
    )

    data class Third(val context: Context) : OnboardingItem(
        image = R.drawable.img_mic_text_grammer,
        title = context.getString(R.string.lbl_avanegar_onboarding_third_title),
        description = buildString {
            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_third_description_first
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_third_description_second
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_third_description_third
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_third_description_fourth
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    R.string.lbl_avanegar_onboarding_third_description_fifth
                ).addBullet()
            )
        }
    )
}