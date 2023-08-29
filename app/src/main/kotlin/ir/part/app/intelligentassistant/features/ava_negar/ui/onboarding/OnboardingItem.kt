package ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding

import android.content.Context
import androidx.annotation.DrawableRes
import ir.part.app.intelligentassistant.features.home.onboarding.addBullet
import ir.part.app.intelligentassistant.R as AiResource

sealed class OnboardingItem(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {

    data class First(val context: Context) : OnboardingItem(
        image = AiResource.drawable.img_avanegar_onboarding_mic,
        title = context.getString(AiResource.string.lbl_avanegar_onboarding_first_title),
        description = context.getString(AiResource.string.lbl_avanegar_onboarding_first_description)
    )

    data class Second(val context: Context) : OnboardingItem(
        image = AiResource.drawable.img_avanegar_onboarding_rubic,
        title = context.getString(AiResource.string.lbl_avanegar_onboarding_second_title),
        description = buildString {
            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_first
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_second
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_third
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_fourth
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_fifth
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_second_description_sixth
                ).addBullet()
            )
        }
    )

    data class Third(val context: Context) : OnboardingItem(
        image = AiResource.drawable.img_avanegar_onboarding_cub,
        title = context.getString(AiResource.string.lbl_avanegar_onboarding_third_title),
        description = buildString {
            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_third_description_first
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_third_description_second
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_third_description_third
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_third_description_fourth
                ).addBullet()
            )
            append("\n")

            append(
                context.getString(
                    AiResource.string.lbl_avanegar_onboarding_third_description_fifth
                ).addBullet()
            )
        }
    )
}