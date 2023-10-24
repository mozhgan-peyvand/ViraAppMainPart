package ai.ivira.app.features.ava_negar.ui

import ai.ivira.app.utils.ui.analytics.events.EventParams
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent
import ai.ivira.app.utils.ui.analytics.events.OnboardingEvent.Type
import ai.ivira.app.utils.ui.analytics.events.ScreenViewEvent
import ai.ivira.app.utils.ui.analytics.events.SearchEvent
import ai.ivira.app.utils.ui.analytics.events.SelectItemEvent
import ai.ivira.app.utils.ui.analytics.events.ShareEvent
import ai.ivira.app.utils.ui.analytics.events.SpecialEvent

private const val s = "record_icon"

object AvanegarAnalytics {
    private const val ORIGIN_AVANEGAR = "Avanegar"
    private const val LIST_VIEW_TYPE = "view_type"
    private const val VIEW_TYPE_COLUMN = "column"
    private const val VIEW_TYPE_GRID = "grid"
    private const val CREATE_FILE = "create_file"
    private const val FAB_UPLOAD = "upload"
    private const val FAB_RECORD = "record"
    private const val RECORD_AUDIO_PERMISSION = "permission"
    private const val CHOOSE_FILE = "choose_file"
    private const val FILE_CREATED_ABOVE60_EVENT = "file_created_above60"
    private const val FILE_CREATED_BELOW60_EVENT = "file_created_below60"
    private const val FILE_DURATION_LIMIT_EXCEED_EVENT = "file_duration_exceed"
    private const val UNABLE_TO_READ_AUDIO_DURATION = "unable_to_audio_duration"
    private const val SHARE_METHOD_PDF = "pdf"
    private const val SHARE_METHOD_TXT = "txt"
    private const val SHARE_METHOD_RAW = "raw"
    private const val DELETE_FILE = "delete_file"
    private const val SELECT_GIF = "select_gif"

    // region screenView
    val screenViewArchiveList: ScreenViewEvent
        get() = ScreenViewEvent("Avanegar Archive List", "ArchiveListScreen")

    fun screenViewArchiveDetail(id: String, title: String): ScreenViewEvent {
        return ScreenViewEvent(
            "Avanegar Archive Detail",
            "ArchiveDetailScreen",
            EventParams.ID to id,
            EventParams.NAME to title
        )
    }

    val screenViewVoiceRecord: ScreenViewEvent
        get() = ScreenViewEvent("Avanegar Record", "VoiceRecordingScreen")

    val screenViewSearch: ScreenViewEvent
        get() = ScreenViewEvent("Avanegar Search", "SearchScreen")

    val screenViewOnboarding: ScreenViewEvent
        get() = ScreenViewEvent("Avanegar Onboarding", "OnboardingScreen")
    // endregion screenView

    // region onboarding
    val onboardingStart: OnboardingEvent
        get() = OnboardingEvent(Type.Beginning, AvanegarOnboarding)

    val onboardingEnd: OnboardingEvent
        get() = OnboardingEvent(Type.End, AvanegarOnboarding)
    // endregion onboarding

    // region selectItem
    fun selectListViewType(isGrid: Boolean): SelectItemEvent {
        return SelectItemEvent(
            itemName = LIST_VIEW_TYPE,
            contentType = if (isGrid) VIEW_TYPE_GRID else VIEW_TYPE_COLUMN,
            EventParams.ORIGIN to ORIGIN_AVANEGAR
        )
    }

    val selectUploadFile: SelectItemEvent
        get() = SelectItemEvent(
            itemName = CREATE_FILE,
            contentType = FAB_UPLOAD,
            EventParams.ORIGIN to ORIGIN_AVANEGAR
        )

    fun selectRecordAudio(permission: String): SelectItemEvent {
        return SelectItemEvent(
            itemName = CREATE_FILE,
            contentType = FAB_RECORD,
            EventParams.ORIGIN to ORIGIN_AVANEGAR,
            RECORD_AUDIO_PERMISSION to permission
        )
    }

    val selectChooseFile: SelectItemEvent
        get() = SelectItemEvent(CHOOSE_FILE, null)

    fun selectDeleteFile(fileType: AvanegarFileType): SelectItemEvent {
        return SelectItemEvent(
            itemName = DELETE_FILE,
            contentType = fileType.value,
            EventParams.ORIGIN to ORIGIN_AVANEGAR
        )
    }

    val selectGif: SelectItemEvent
        get() = SelectItemEvent(SELECT_GIF, null)

    fun selectRecordIcon(willRecord: Boolean, hasPaused: Boolean): SelectItemEvent {
        return SelectItemEvent(
            itemName = "record_icon",
            contentType = null,
            "record_status" to "$willRecord:$hasPaused"
        )
    }

    val selectStopRecord: SelectItemEvent
        get() = SelectItemEvent("stop_record", null)
    val selectConvertToText: SelectItemEvent
        get() = SelectItemEvent("convert_to_text", null)
    val selectStartOver: SelectItemEvent
        get() = SelectItemEvent("record_start_over", null)
    val selectDiscardRecorded: SelectItemEvent
        get() = SelectItemEvent("discard_recorded", null)
    // endregion selectItem

    // region specialEvents
    val fileAbove60SecondsCreated: SpecialEvent
        get() = SpecialEvent(FILE_CREATED_ABOVE60_EVENT)
    val fileBelow60SecondsCreated: SpecialEvent
        get() = SpecialEvent(FILE_CREATED_BELOW60_EVENT)
    val fileDurationExceed: SpecialEvent
        get() = SpecialEvent(FILE_DURATION_LIMIT_EXCEED_EVENT)
    val unableToReadFileDuration: SpecialEvent
        get() = SpecialEvent(UNABLE_TO_READ_AUDIO_DURATION)
    // endregion specialEvents

    // region Share
    fun sharePdf(id: String): ShareEvent {
        return ShareEvent(method = SHARE_METHOD_PDF, contentType = ORIGIN_AVANEGAR, itemId = id)
    }

    fun shareTxt(id: String): ShareEvent {
        return ShareEvent(method = SHARE_METHOD_TXT, contentType = ORIGIN_AVANEGAR, itemId = id)
    }

    fun shareRawText(id: String): ShareEvent {
        return ShareEvent(method = SHARE_METHOD_RAW, contentType = ORIGIN_AVANEGAR, itemId = id)
    }
    // endregion Share

    // region search
    fun search(searchTerm: String): SearchEvent {
        return SearchEvent(searchTerm)
    }
    // endregion search

    object AvanegarOnboarding : OnboardingEvent.Origin {
        override val origin: String = ORIGIN_AVANEGAR
    }

    enum class AvanegarFileType(val value: String) {
        Uploading("uploading"),
        Tracking("tracking"),
        Processed("processed")
    }
}