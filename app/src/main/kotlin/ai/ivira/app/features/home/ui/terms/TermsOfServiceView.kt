package ai.ivira.app.features.home.ui.terms

import ai.ivira.app.R
import androidx.annotation.StringRes

data class TermsOfServiceView(
    @StringRes val header: Int,
    @StringRes val description: List<Int>
) {
    companion object {
        val descriptionList = listOf(
            TermsOfServiceView(
                header = R.string.lbl_terms_rules,
                description = listOf(R.string.lbl_terms_rules_part1)
            ),
            TermsOfServiceView(
                header = R.string.lbl_terms_terminology,
                description = listOf(
                    R.string.lbl_terms_terminology_vira,
                    R.string.lbl_terms_terminology_user,
                    R.string.lbl_terms_terminology_user_account,
                    R.string.lbl_terms_terminology_confidential_information
                )
            ),
            TermsOfServiceView(
                header = R.string.lbl_terms_conditions,
                description = listOf(R.string.lbl_terms_conditions_part1)
            ),
            TermsOfServiceView(
                header = R.string.lbl_terms_respecting_privacy,
                description = listOf(
                    R.string.lbl_terms_respecting_privacy_part1,
                    R.string.lbl_terms_respecting_privacy_part2
                )
            ),
            TermsOfServiceView(
                header = R.string.lbl_terms_user_responsibility,
                description = listOf(
                    R.string.lbl_terms_user_responsibility_part1,
                    R.string.lbl_terms_user_responsibility_part2,
                    R.string.lbl_terms_user_responsibility_part3
                )
            )
        )
    }
}