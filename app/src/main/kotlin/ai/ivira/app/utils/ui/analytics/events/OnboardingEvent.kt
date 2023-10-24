package ai.ivira.app.utils.ui.analytics.events

import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent.Type.Beginning
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent.Type.End

data class OnboardingEvent(
    val type: Type,
    val origin: Origin
) {
    val eventName: String
        get() = when (type) {
            Beginning -> EventNames.ONBOARDING_BEGIN
            End -> EventNames.ONBOARDING_END
        }

    enum class Type {
        Beginning,
        End
    }

    interface Origin {
        val origin: String
    }
}