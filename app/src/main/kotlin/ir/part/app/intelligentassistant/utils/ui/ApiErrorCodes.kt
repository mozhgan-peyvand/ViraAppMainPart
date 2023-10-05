package ir.part.app.intelligentassistant.utils.ui

enum class ApiErrorCodes(val value: String) {
    InvalidInputData("invalidInputData"),
    TokenNotProvided("tokenNotProvided"),
    InvalidToken("invalidToken"),
    UrlNotFound("invalidToken"),
    VPNConnectedError("VPNConnectedError")
}