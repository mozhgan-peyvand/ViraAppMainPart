package ai.ivira.app.features.avasho.ui

import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object AvashoAnalytics {
    private const val PREFIX = "avasho"

    // region specialEvents
    val uploadIconClick: SpecialEvent
        get() = SpecialEvent("${PREFIX}_upload_icon")

    val createFileAbove1k: SpecialEvent
        get() = SpecialEvent("${PREFIX}_file_created_above1k")
    val createFileBelow1k: SpecialEvent
        get() = SpecialEvent("${PREFIX}_file_created_below1k")

    val cancelUploadFile: SpecialEvent
        get() = SpecialEvent("${PREFIX}_cancel_upload")
    val cancelTrackFile: SpecialEvent
        get() = SpecialEvent("${PREFIX}_cancel_track")
    val playItem: SpecialEvent
        get() = SpecialEvent("${PREFIX}_play_icon")
    val downloadItem: SpecialEvent
        get() = SpecialEvent("${PREFIX}_download_item")
    val shareItem: SpecialEvent
        get() = SpecialEvent("${PREFIX}_share_item")
    // endregion specialEvents
}