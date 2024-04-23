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
                header = R.string.lbl_terms_of_services,
                description = listOf(R.string.lbl_terms_of_service_details)
            ),
            TermsOfServiceView(
                header = R.string.lbl_terminology,
                description = listOf(
                    R.string.lbl_terminology_vira,
                    R.string.lbl_terminology_user,
                    R.string.lbl_terminology_commercial_side,
                    R.string.lbl_terminology_user_account,
                    R.string.lbl_terminology_confidential_information
                )
            ),
            TermsOfServiceView(
                header = R.string.lbl_terms_and_conditions_of_using_vira,
                description = listOf(
                    R.string.lbl_using_provided_services_means_acceptance_of_rules,
                    R.string.lbl_rules_and_procedures_are_based_on_iran_rules
                )
            ),
            TermsOfServiceView(
                header = R.string.lbl_respect_privacy,
                description = listOf(
                    R.string.lbl_accessing_microphone,
                    R.string.lbl_respect_uses_privacy
                )
            )
        )
    }
}