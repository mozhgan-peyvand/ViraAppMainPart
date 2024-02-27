package ai.ivira.app.features.imazh.ui

import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

object ImazhAnalytics {
    private const val PREFIX = "imazh"

    // region screenView
    val screenViewArchiveList: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Archive", "ImazhArchiveListScreen")

    val screenViewDetails: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Detail", "ImazhDetailsScreen")

    val screenViewNewImageDescriptor: ScreenViewEvent
        get() = ScreenViewEvent("Imazh ImageDescriptor", "ImazhNewImageDescriptorScreen")

    val screenViewOnboarding: ScreenViewEvent
        get() = ScreenViewEvent("Imazh Onboarding", "ImazhOnboardingScreen")

    // endregion screenView

    // region specialEvents
    val createFile: SpecialEvent
        get() = SpecialEvent("${PREFIX}_file_created")
    val addKeywords: SpecialEvent
        get() = SpecialEvent("${PREFIX}_add_keywords")
    val selectStyle: SpecialEvent
        get() = SpecialEvent("${PREFIX}_select_style")
    val sharePicture: SpecialEvent
        get() = SpecialEvent("${PREFIX}_share_picture")
    val downloadPicture: SpecialEvent
        get() = SpecialEvent("${PREFIX}_download_picture")
    // endregion specialEvents
}