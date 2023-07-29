package ir.part.app.intelligentassistant.ui.navigation

sealed class ScreensRouter(val router: String) {
    object HomeScreen : ScreensRouter("home_screen")
    object ForceUpdateScreen : ScreensRouter("forceUpdate_screen")
    object AvaNegarVoiceRecordingScreen :
        ScreensRouter("avaNegarVoiceRecording_screen")

    object AvaNegarSearchScreen : ScreensRouter("avaNegarSearch_screen")
    object AvaNegarProcessedArchiveDetailScreen :
        ScreensRouter("avaNegarProcessedArchiveDetail_screen")

    object AvaNegarOnBoardingScreen :
        ScreensRouter("avaNegarOnBoarding_screen")

    object AvaNegarArchiveScreen : ScreensRouter("avaNegarArchive_screen")
}
