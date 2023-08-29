package ir.part.app.intelligentassistant.features.home.onboarding

import android.content.Context
import androidx.annotation.DrawableRes
import ir.part.app.intelligentassistant.R as AiResource

sealed class MainOnboardingItem(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: String
) {
    data class First(private val context: Context) : MainOnboardingItem(
        image = AiResource.drawable.img_main_onboarding_horn,
        title = AiResource.string.lbl_avanegar_service,
        description = context.getString(AiResource.string.lbl_avanegar_service_details)
    )

    data class Second(private val context: Context) : MainOnboardingItem(
        image = AiResource.drawable.img_main_onboarding_lamp,
        title = AiResource.string.lbl_soon_in_vira,
        description = context.getString(AiResource.string.lbl_soon_in_vira_description_first).addBullet().goNextLine().plus(
            context.getString(AiResource.string.lbl_soon_in_vira_description_second).addBullet().goNextLine().plus(
                context.getString(AiResource.string.lbl_soon_in_vira_description_third).addBullet().goNextLine().plus(
                    context.getString(AiResource.string.lbl_soon_in_vira_description_fourth).addBullet()
                )
            )
        )
    )
}
fun String.addBullet(): String {
    return "•   $this"
}
fun String.goNextLine(): String {
    return "$this \n"
}