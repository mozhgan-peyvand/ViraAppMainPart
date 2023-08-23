package ir.part.app.intelligentassistant.features.ava_negar.ui.onboarding

import androidx.annotation.DrawableRes
import ir.part.app.intelligentassistant.R as AiResource

sealed class OnboardingItem(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: Int
) {

    object First : OnboardingItem(
        image = AiResource.drawable.img_bwink_edu,
        title = AiResource.string.lbl_welcome_on_boarding,
        description = AiResource.string.lbl_on_boarding_first_slide_description
    )

    object Second : OnboardingItem(
        image = AiResource.drawable.img_mobile,
        title = AiResource.string.lbl_support_accent,
        description = AiResource.string.lbl_on_boarding_second_slide_description
    )

    object Third : OnboardingItem(
        image = AiResource.drawable.img_creative_design,
        title = AiResource.string.lbl_different_format,
        description = AiResource.string.lbl_on_boarding_third_slide_description
    )

}