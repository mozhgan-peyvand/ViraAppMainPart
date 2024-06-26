package ai.ivira.app.features.home.ui.home.sheets

enum class HomeItemBottomSheetType(val value: String) {
    NotificationPermission("notificationPermission"),
    UpdateApp("update"),
    ForceUpdate("forceUpdate"),
    Changelog("changelog"),
    UnavailableTile("unavailableFeature"),
    LogoutConfirmation("logoutConfirmation")
}