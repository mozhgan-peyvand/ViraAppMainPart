package ai.ivira.app.utils.ui.analytics.events

class ScreenViewEvent(
    /** A Human-readable name for this screen like HomeScreen */
    val screenName: String,
    /** A developer friendlyName for this event. this is the name of composable
     * which indicates the screen */
    val screenClass: String,
    /** extra params like item_id which is used in only some screens*/
    vararg val params: Pair<String, String>
)