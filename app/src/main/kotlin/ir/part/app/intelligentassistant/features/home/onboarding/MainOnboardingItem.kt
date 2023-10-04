package ir.part.app.intelligentassistant.features.home.onboarding

import android.content.Context
import androidx.annotation.DrawableRes
import ir.part.app.intelligentassistant.R

sealed class MainOnboardingItem(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: String
) {
    data class First(private val context: Context) : MainOnboardingItem(
        image = R.drawable.img_mic_text,
        title = R.string.lbl_avanegar_service,
        description = context.getString(R.string.lbl_avanegar_service_details)
    )

    data class Second(private val context: Context) : MainOnboardingItem(
        image = R.drawable.img_ai_tools,
        title = R.string.lbl_soon_in_vira,
        description = buildString {
            append(
                context.getString(
                    R.string.lbl_soon_in_vira_description_first
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_soon_in_vira_description_second
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_soon_in_vira_description_third
                ).addBullet()
            )

            append("\n")

            append(
                context.getString(
                    R.string.lbl_soon_in_vira_description_fourth
                ).addBullet()
            )
        }
    )
}

fun String.addBullet(): String {
    return "•   $this"
}