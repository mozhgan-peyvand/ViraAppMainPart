package ai.ivira.app.utils.ui.analytics.events

class SpecialEvent(
    val eventName: String,
    vararg val params: Pair<String, String>
)