package ai.ivira.app.features.avasho.ui

import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object AvashoAnalytics {
    private const val PREFIX = "avasho"

    // region screenView
    val screenViewArchiveList: ScreenViewEvent
        get() = ScreenViewEvent("Avasho Archive", "AvashoArchiveListScreen")

    val screenViewFileCreation: ScreenViewEvent
        get() = ScreenViewEvent("Avasho New Voice", "AvashoFileCreationScreen")

    val screenViewOnboarding: ScreenViewEvent
        get() = ScreenViewEvent("Avasho Onboarding", "AvashoOnboardingScreen")

    val screenViewSearch: ScreenViewEvent
        get() = ScreenViewEvent("Avasho Search", "AvashoSearchScreen")

    // endregion

    // region specialEvents
    val uploadIconClick: SpecialEvent
        get() = SpecialEvent("${PREFIX}_upload_icon")

    val createFileAbove1k: SpecialEvent
        get() = SpecialEvent("${PREFIX}_file_created_above1k_v2")
    val createFileBelow1k: SpecialEvent
        get() = SpecialEvent("${PREFIX}_file_created_below1k_v2")

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