package ai.ivira.app.utils.ui.analytics.events

class SelectItemEvent(
    val itemName: String,
    val contentType: String?,
    vararg val params: Pair<String, String>
)