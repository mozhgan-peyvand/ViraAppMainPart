package ai.ivira.app.features.ava_negar

sealed class AvanegarScreenRoutes(val route: String) {
    data object AvaNegarVoiceRecording :
        AvanegarScreenRoutes("avaNegarVoiceRecording_screen")

    data object AvaNegarSearch : AvanegarScreenRoutes("avaNegarSearch_screen")
    data object AvaNegarArchiveDetail :
        AvanegarScreenRoutes("avaNegarProcessedArchiveDetail_screen?id={id}&title={title}") {
        fun createRoute(id: Int, title: String): String {
            return "avaNegarProcessedArchiveDetail_screen?id=$id&title=$title"
        }
    }

    data object AvaNegarOnboarding :
        AvanegarScreenRoutes("avaNegarOnBoarding_screen")

    data object AvaNegarArchiveList : AvanegarScreenRoutes("avaNegarArchive_screen")
}