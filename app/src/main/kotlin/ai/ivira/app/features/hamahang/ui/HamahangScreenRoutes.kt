package ai.ivira.app.features.hamahang.ui

sealed class HamahangScreenRoutes(val route: String) {
    data object HamahangNewAudioScreen : HamahangScreenRoutes("hamahangNewAudio_screen")
    data object HamahangArchiveListScreen : HamahangScreenRoutes("hamahangArchiveList_screen")
    data object HamahangDetailScreen : HamahangScreenRoutes("hamahangDetail_screen?id={id}") {
        fun createRoute(id: String): String {
            return "hamahangDetail_screen?id=$id"
        }
    }
}